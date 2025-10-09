package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.OperationLog;
import myself.potato.mapper.OperationLogMapper;
import myself.potato.service.IOperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("operationLogServiceImpl")
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {


    @Async // 保持异步执行
    public void saveLog(OperationLog log) {
        // 使用 this.save() 方法，它来自于 IService
        this.save(log);
    }

}
