package life.zhaohuan.community.community.service;

import life.zhaohuan.community.community.dto.PaginationDTO;
import life.zhaohuan.community.community.dto.QuestionDTO;
import life.zhaohuan.community.community.mapper.QuestionMapper;
import life.zhaohuan.community.community.mapper.UserMapper;
import life.zhaohuan.community.community.model.Question;
import life.zhaohuan.community.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Integer page, Integer size) {
        //size*(page-1)
        Integer offset = size * (page - 1);
        List<Question> questions = questionMapper.list(offset , size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question , questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        Integer totalCount = questionMapper.count();
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }
}
