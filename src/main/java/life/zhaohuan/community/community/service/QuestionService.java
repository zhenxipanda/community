package life.zhaohuan.community.community.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import life.zhaohuan.community.community.dto.PaginationDTO;
import life.zhaohuan.community.community.dto.QuestionDTO;
import life.zhaohuan.community.community.dto.QuestionQueryDTO;
import life.zhaohuan.community.community.enums.SortEnum;
import life.zhaohuan.community.community.exception.CustomizedErrorCode;
import life.zhaohuan.community.community.exception.CustomizedException;
import life.zhaohuan.community.community.mapper.QuestionExtMapper;
import life.zhaohuan.community.community.mapper.QuestionMapper;
import life.zhaohuan.community.community.mapper.UserMapper;
import life.zhaohuan.community.community.model.Question;
import life.zhaohuan.community.community.model.QuestionExample;
import life.zhaohuan.community.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    // 使用 PageHelper 实现分页，但是因为返回值类型，丢失了用户的头像
    public PageInfo<QuestionDTO> getList(String search , Integer page , Integer size){
        if(StringUtils.isNotBlank(search)){
            String[] tags = StringUtils.split(search , " ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }
        PaginationDTO paginationDTO = new PaginationDTO();

        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setPage(page);
        questionQueryDTO.setSize(size);

        PageHelper.startPage(page, size);
        List<Question> questions = questionExtMapper.selectBySearchNotLimit(questionQueryDTO);

        PageInfo pageInfo = new PageInfo(questions);
        System.out.println(pageInfo.getTotal());
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        // 但是这部分好像没起作用
        for (Question question : questions) {
            // 根据question自带的creator 得到用户id，根据用户id查询得到用户 user
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            // 将question 中的属性快速复制给 questionDTO
            BeanUtils.copyProperties(question , questionDTO);
            // 只有questionDTO 的 user 属性还未赋值，所以set进去
            questionDTO.setUser(user);
            // 将赋值完毕的 questionDTO 放入 questionDTOList 中
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        PageInfo<QuestionDTO> p = new PageInfo<>(questionDTOList);
        BeanUtils.copyProperties(pageInfo,p);
        // 返回 的p 还是Question类型
        return p;

    }

    // 将问题按照搜索，页码，每页数量，展示在首页
    public PaginationDTO list(String search, String tag, String sort, Integer page, Integer size) {
        // 如果不使用搜索功能,search is null，不会进入if
        if(StringUtils.isNotBlank(search)){
            String[] tags = StringUtils.split(search , " ");
            search = Arrays
                    .stream(tags)
                    .filter(StringUtils::isNotBlank)
                    .map(t -> t.replace("+","").replace("*","").replace("?",""))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("|"));
        }
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;

        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        if (StringUtils.isNotBlank(tag)) {
            tag = tag.replace("+", "").replace("*", "").replace("?", "");
            questionQueryDTO.setTag(tag);
        }
        for (SortEnum sortEnum : SortEnum.values()) {
            if(sortEnum.name().toLowerCase().equals(sort)){
                questionQueryDTO.setSort(sort);
                if(sortEnum == sortEnum.HOT7){
                    questionQueryDTO.setTime(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 7);
                }
                if(sortEnum == sortEnum.HOT30){
                    questionQueryDTO.setTime(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
                }
                break;
            }
        }

        // totalCount 是数据库中 的条数 ，也就是问题的个数，行数
//        自己定义的函数
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }
        else{
            totalPage = totalCount / size + 1;
        }
        // 防止在网址上非法输入，page虽然默认是1，但是点击下一个时，page就会改变
        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }
        // totalPage 是总页数 ，设置是否展示首页，前一页，下一页，尾页
        paginationDTO.setPagination(totalPage,page);

        //size*(page-1)
        Integer offset = page < 1 ? 0 : size * (page - 1);
        // 计算offset 是为了 分页显示 limit offset , size

        // 设置问题为按创建时间倒序 这里设置没有用，因为questionExample并没有传到数据库去查询，倒序展示，是因为数据库查询时加了order by
//        QuestionExample questionExample = new QuestionExample();
//        questionExample.setOrderByClause("gmt_create desc");
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        // 去数据库中查询 满足条件的 questions 也就是此页要展示的question
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);
        // 新建放 QuestionDTO 的 列表
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            // 根据question自带的creator 得到用户id，根据用户id查询得到用户 user
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            // 将question 中的属性快速复制给 questionDTO
            BeanUtils.copyProperties(question , questionDTO);
            // 只有questionDTO 的 user 属性还未赋值，所以set进去
            questionDTO.setUser(user);
            // 将赋值完毕的 questionDTO 放入 questionDTOList 中
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        return paginationDTO;
    }

    // 在profileController 调用，获取当前用户所提的问题
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
//        根据用户id 去数据库question表中查该用户创建的问题总数
//        这次查询时获得总数，Integer类型
        Integer totalCount = (int) questionMapper.countByExample(questionExample);
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }
        else{
            totalPage = totalCount / size + 1;
        }

        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }

        paginationDTO.setPagination(totalPage,page);
        //size*(page-1)
        Integer offset = size * (page - 1);
//        这次查询是查询出所有的问题列表
        // 自己修复的
        // 看看能否 profile.html 的 我的问题是 按时间倒序 可以诶！
        questionExample.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(questionExample, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
//            通过主键查询，问题的创建者，得到用户user
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question , questionDTO);
//            将用户添加到questionDTO
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

//    根据问题 id 查询出这条问题
    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null){
            throw new CustomizedException(CustomizedErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question , questionDTO);
//        同样的，为了展示用户名称，需要查询到这个问题的创建者
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null){
            // 创建 向数据库传递的是question对象 都是insert()
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }
        else{
            // 更新
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
//            更新都是两个参数，一个是新建的对象，设置了新属性，一个是从数据库中查询出来的原来Id,更新，不用新插入
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if(updated != 1){
                throw new CustomizedException(CustomizedErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

//    累加阅读数
    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        String regexTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexTag);
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
