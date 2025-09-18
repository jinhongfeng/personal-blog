package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.ConcernInfo;
import myself.potato.mapper.ConcernInfoMapper;
import myself.potato.service.IConcernInfoService;
import org.springframework.stereotype.Service;

@Service("concernInfoService")
public class ConcernInfoServiceImpl extends ServiceImpl<ConcernInfoMapper, ConcernInfo> implements IConcernInfoService {


}
