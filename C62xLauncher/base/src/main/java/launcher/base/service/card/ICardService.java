package launcher.base.service.card;

import java.util.List;

public interface ICardService<T extends ICard > {
    List<T> getAllCards();
}
