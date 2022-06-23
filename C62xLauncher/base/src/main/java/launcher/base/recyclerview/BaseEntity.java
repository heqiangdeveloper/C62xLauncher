package launcher.base.recyclerview;

public class BaseEntity {
    private int viewType;
    private int itemLayoutId;


    public BaseEntity(int viewType, int itemLayoutId) {
        this.viewType = viewType;
        this.itemLayoutId = itemLayoutId;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getItemLayoutId() {
        return itemLayoutId;
    }

    public void setItemLayoutId(int itemLayoutId) {
        this.itemLayoutId = itemLayoutId;
    }
}
