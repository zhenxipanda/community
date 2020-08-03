package life.zhaohuan.community.community.service;

import life.zhaohuan.community.community.mapper.UserMapper;
import life.zhaohuan.community.community.model.User;
import life.zhaohuan.community.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
//        传入的这个user 信息不完整，在AuthorizedController中只设置了部分属性
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
//        通过user的github id(account_id) 去user表查询user信息
//        如果之前登陆过，user表中一定有account_id
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size() == 0){
            // insert 如果不存在，将之前未设置的属性设置
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }
        else{
            // update 如果存在，得到他，即dbUser ，它的信息全，然后新建一个对象，
            User dbUser = users.get(0);
            User updateUser = new User();
//            更新的话，创建一个新的user对象，修改更新时间，头像可能变化，名字可能变化，
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            UserExample example = new UserExample();
            example.createCriteria()
                    .andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, example);
        }
    }
}
