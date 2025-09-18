package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.Circle;
import myself.potato.mapper.CircleMapper;
import myself.potato.service.ICircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("circleService")
public class CircleServiceImpl extends ServiceImpl<CircleMapper, Circle> implements ICircleService {

    @Autowired
    private CircleMapper circleMapper;

    @Override
    public void incrementHasCount(Integer id) {
        circleMapper.incrementHasCount(id);
    }
}
