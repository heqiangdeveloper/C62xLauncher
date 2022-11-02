package launcher.base.service.platform;

public class PlatformService implements IPlatformService{

    private boolean mUseAutoMachine = true;

    @Override
    public boolean isAosp() {
        return !mUseAutoMachine;
    }

    @Override
    public boolean isPhone() {
        return !mUseAutoMachine;
    }

    @Override
    public boolean isC62x() {
        return mUseAutoMachine;
    }
}
