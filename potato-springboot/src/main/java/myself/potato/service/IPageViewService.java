package myself.potato.service;


import com.baomidou.mybatisplus.extension.service.IService;
import myself.potato.entity.PageView;

public interface IPageViewService extends IService<PageView> {
    /**
     * 记录一次页面访问
     * @param pageUri 页面的URI
     */
    void recordView(String pageUri);

    /**
     * 将Redis中的数据同步到数据库
     */
    void syncToDatabase();
}
