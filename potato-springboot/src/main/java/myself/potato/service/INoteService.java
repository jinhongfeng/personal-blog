package myself.potato.service;

import com.baomidou.mybatisplus.extension.service.IService;
import myself.potato.entity.Note;

public interface INoteService extends IService<Note> {

    void incrementPageview(Integer id);

}
