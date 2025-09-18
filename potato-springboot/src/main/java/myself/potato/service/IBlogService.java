package myself.potato.service;

import com.baomidou.mybatisplus.extension.service.IService;
import myself.potato.entity.Blog;

public interface IBlogService extends IService<Blog> {

    void incrementPageview(Integer id);

}
