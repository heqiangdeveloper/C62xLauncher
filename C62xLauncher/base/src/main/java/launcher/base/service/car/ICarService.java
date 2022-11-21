package launcher.base.service.car;

public interface ICarService {

    boolean isConnect();

    String getCarType();

    int getOffLineCfg();

    String getVinCode();

    boolean isHasDVR();

    boolean doSwitchWindow(boolean isOpenCmd);
}
