package myself.potato.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.Apply;
import myself.potato.mapper.ApplyMapper;
import myself.potato.service.IApplyService;
import org.springframework.stereotype.Service;


@Service("applyService")
public class ApplyServiceImpl extends ServiceImpl<ApplyMapper, Apply> implements IApplyService {

}
