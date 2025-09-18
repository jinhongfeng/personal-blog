package myself.potato.service;

import com.baomidou.mybatisplus.extension.service.IService;
import myself.potato.entity.TravelImage;

import java.util.List;

public interface ITravelImageService extends IService<TravelImage> {

    // 根据圈子ID查询图片列表（按sort升序）
    List<TravelImage> listByTravelId(Integer travelId);




    // 更新图片排序
    boolean updateSort(Integer travelId, List<Integer> imageIds);

    // 根据圈子ID删除所有关联图片
    boolean removeByTravelId(Integer travelId);
}
