package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.Concern;
import myself.potato.mapper.ConcernMapper;
import myself.potato.service.IConcernService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("concernService")
public class ConcernServiceImpl extends ServiceImpl<ConcernMapper, Concern> implements IConcernService {

    @Autowired
    private ConcernMapper concernMapper;


}
