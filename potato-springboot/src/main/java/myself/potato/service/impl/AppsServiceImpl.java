package myself.potato.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.Apps;
import myself.potato.mapper.AppsMapper;
import myself.potato.service.IAppsService;
import org.springframework.stereotype.Service;


@Service("appsService")
public class AppsServiceImpl extends ServiceImpl<AppsMapper, Apps> implements IAppsService {

}
