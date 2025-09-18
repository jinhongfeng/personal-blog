package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.LoveMessage;
import myself.potato.mapper.LoveMessageMapper;
import myself.potato.service.ILoveMessageService;
import org.springframework.stereotype.Service;

@Service("lovemessageServiceImpl")
public class LoveMessageImpl extends ServiceImpl<LoveMessageMapper, LoveMessage> implements ILoveMessageService {


}
