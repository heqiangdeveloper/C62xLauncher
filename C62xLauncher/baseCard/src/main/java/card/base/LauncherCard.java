package card.base;

import android.content.Context;
import android.view.View;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import launcher.base.service.card.ICard;

@Entity
public class LauncherCard implements ICard {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int position;
    private boolean inHome;
    private String name;
    private int type;
    private boolean canExpand;
    private String mSelectBgResName;
    private String mUnselectBgResName;
    @Ignore
    private int mSelectBgRes;
    @Ignore
    private int mUnselectBgRes;


    @Ignore
    private ICardViewCreator mCardViewCreator;

    public int getSelectBgRes() {
        return mSelectBgRes;
    }

    public void setSelectBgRes(int selectBgRes) {
        mSelectBgRes = selectBgRes;
    }

    public int getUnselectBgRes() {
        return mUnselectBgRes;
    }

    public void setUnselectBgRes(int unselectBgRes) {
        mUnselectBgRes = unselectBgRes;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isInHome() {
        return inHome;
    }

    public void setInHome(boolean inHome) {
        this.inHome = inHome;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCanExpand() {
        return canExpand;
    }

    public void setCanExpand(boolean canExpand) {
        this.canExpand = canExpand;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getSelectBgResName() {
        return mSelectBgResName;
    }

    public void setSelectBgResName(String selectBgResName) {
        mSelectBgResName = selectBgResName;
    }

    public String getUnselectBgResName() {
        return mUnselectBgResName;
    }

    public void setUnselectBgResName(String unselectBgResName) {
        mUnselectBgResName = unselectBgResName;
    }

    public ICardViewCreator getCardViewCreator() {
        return mCardViewCreator;
    }

    public void setCardViewCreator(ICardViewCreator cardViewCreator) {
        mCardViewCreator = cardViewCreator;
    }
    public View getLayout(Context context){
        if (mCardViewCreator == null) {
            return new View(context);
        }
        return mCardViewCreator.createCardView(context);
    }

    @Override
    public String toString() {
        return "LauncherCard{" +
                "name='" + name + '\'' +
                '}';
    }
}
