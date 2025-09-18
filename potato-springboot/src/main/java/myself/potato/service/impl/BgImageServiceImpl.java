package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.BgImage;
import myself.potato.mapper.BgImageMapper;
import myself.potato.service.IBgImageService;
import org.springframework.stereotype.Service;

@Service("bgImageService")
public class BgImageServiceImpl extends ServiceImpl<BgImageMapper, BgImage> implements IBgImageService {

}
