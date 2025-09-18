package myself.potato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myself.potato.entity.CircleImage;
import myself.potato.mapper.CircleImageMapper;
import myself.potato.service.ICircleImageService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("circleImageService")
public class CircleImageServiceImpl extends ServiceImpl<CircleImageMapper, CircleImage> implements ICircleImageService {

    /**
     * 根据圈子ID查询图片列表，按sort升序排列
     */
    @Override
    public List<CircleImage> listByCircleId(Integer circleId) {
        LambdaQueryWrapper<CircleImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CircleImage::getCircleId, circleId)
                .orderByAsc(CircleImage::getSort); // 按排序号升序，保证上传顺序
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据圈子ID删除所有关联图片
     */
    @Override
    public boolean removeByCircleId(Integer circleId) {
        LambdaQueryWrapper<CircleImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CircleImage::getCircleId, circleId);
        int rows = baseMapper.delete(queryWrapper);
        return rows > 0;
    }

    /**
     * 更新图片排序
     * @param circleId 圈子ID
     * @param imageIds 按新顺序排列的图片ID列表
     */
    @Override
    public boolean updateSort(Integer circleId, List<Integer> imageIds) {
        // 遍历图片ID列表，按顺序更新sort值
        for (int i = 0; i < imageIds.size(); i++) {
            CircleImage image = new CircleImage();
            image.setId(imageIds.get(i));
            image.setSort(i + 1);
            baseMapper.updateById(image);
        }
        return true;
    }
}
