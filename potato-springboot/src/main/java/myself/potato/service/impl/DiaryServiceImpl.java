package myself.potato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.Diary;
import myself.potato.mapper.DiaryMapper;
import myself.potato.service.IDiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiaryServiceImpl extends ServiceImpl<DiaryMapper, Diary> implements IDiaryService {

    @Autowired
    private DiaryMapper diaryMapper;

    @Override
    public void incrementPageview(Integer id) {
        diaryMapper.incrementPageView(id);
    }

}

