package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.Note;
import myself.potato.mapper.NoteMapper;
import myself.potato.service.INoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements INoteService {

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public void incrementPageview(Integer id) {
        noteMapper.incrementPageView(id);
    }
}
