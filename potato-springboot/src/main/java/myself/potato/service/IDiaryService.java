package myself.potato.service;

import com.baomidou.mybatisplus.extension.service.IService;
import myself.potato.entity.Diary;

public interface IDiaryService extends IService<Diary> {

    void incrementPageview(Integer id);


}
