package myself.potato.service;

import com.baomidou.mybatisplus.extension.service.IService;
import myself.potato.entity.CircleImage;

import java.util.List;

public interface ICircleImageService extends IService<CircleImage> {

    // 根据圈子ID查询图片列表（按sort升序）
    List<CircleImage> listByCircleId(Integer circleId);

    // 根据圈子ID删除所有关联图片
    boolean removeByCircleId(Integer circleId);

    // 更新图片排序
    boolean updateSort(Integer circleId, List<Integer> imageIds);

}
