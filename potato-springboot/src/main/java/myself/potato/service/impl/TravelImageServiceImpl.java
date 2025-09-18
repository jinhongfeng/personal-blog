package myself.potato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.CircleImage;
import myself.potato.mapper.TravelImageMapper;
import myself.potato.service.ITravelImageService;
import org.springframework.stereotype.Service;
import myself.potato.entity.TravelImage;

import java.util.List;

@Service("travelImageService")
public class TravelImageServiceImpl extends ServiceImpl<TravelImageMapper, TravelImage> implements ITravelImageService {

    /**
     * 根据travelId查询图片列表，按sort升序排列
     */
    @Override
    public List<TravelImage> listByTravelId(Integer travelId) {
        LambdaQueryWrapper<TravelImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TravelImage::getTravelId, travelId)
                .orderByAsc(TravelImage::getSort); // 按排序号升序，保证上传顺序
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据圈子ID删除所有关联图片
     */
    @Override
    public boolean removeByTravelId(Integer travelId) {
        LambdaQueryWrapper<TravelImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TravelImage::getTravelId, travelId);
        int rows = baseMapper.delete(queryWrapper);
        return rows > 0;
    }

    /**
     * 更新图片排序
     * @param travelId 旅游相片Id
     * @param imageIds 按新顺序排列的图片ID列表
     */
    @Override
    public boolean updateSort(Integer travelId, List<Integer> imageIds) {
        // 遍历图片ID列表，按顺序更新sort值
        for (int i = 0; i < imageIds.size(); i++) {
            TravelImage image = new TravelImage();
            image.setId(imageIds.get(i));
            image.setSort(i + 1);
            baseMapper.updateById(image);
        }
        return true;
    }

}
