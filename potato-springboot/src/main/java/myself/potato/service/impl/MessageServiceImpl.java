package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.LeaveMessage;
import myself.potato.mapper.MessageMapper;

import myself.potato.service.IMessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, LeaveMessage> implements IMessageService {


}
