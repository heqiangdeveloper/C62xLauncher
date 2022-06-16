package card.db;

import java.util.List;

import card.base.LauncherCard;

public interface IQueryListener {
    void onSuccess(List<LauncherCard> cardList);
}
