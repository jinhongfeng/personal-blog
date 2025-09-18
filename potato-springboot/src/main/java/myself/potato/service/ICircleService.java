package myself.potato.service;

import com.baomidou.mybatisplus.extension.service.IService;
import myself.potato.entity.Circle;

public interface ICircleService extends IService<Circle> {


    void incrementHasCount(Integer id);
}

