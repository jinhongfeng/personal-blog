package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.Travel;
import myself.potato.mapper.TravelMapper;
import myself.potato.service.ITravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("travelService")
public class TravelServiceImpl extends ServiceImpl<TravelMapper, Travel> implements ITravelService {

    @Autowired
    private TravelMapper travelMapper;

}
