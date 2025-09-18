package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.Music;
import myself.potato.mapper.MusicMapper;
import myself.potato.service.IMusicService;
import org.springframework.stereotype.Service;

@Service("musicService")
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements IMusicService {


}
