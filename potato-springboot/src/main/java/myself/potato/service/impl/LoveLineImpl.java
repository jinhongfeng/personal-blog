package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.LoveLine;
import myself.potato.mapper.LoveLineMapper;
import myself.potato.service.ILoveLineService;
import org.springframework.stereotype.Service;

@Service("lovelineServiceImpl")
public class LoveLineImpl extends ServiceImpl<LoveLineMapper, LoveLine> implements ILoveLineService {


}
