package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.Blog;
import myself.potato.mapper.BlogMapper;
import myself.potato.service.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("blogServiceImpl")
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Override
    public void incrementPageview(Integer id) {
        blogMapper.incrementPageView(id);
    }
}
