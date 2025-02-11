package sandjentrance.com.sj.dagger;

import javax.inject.Singleton;

import dagger.Component;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.ui.BaseActivity;
import sandjentrance.com.sj.ui.BaseDialogFrag;
import sandjentrance.com.sj.ui.BaseFullScreenDialogFrag;
import sandjentrance.com.sj.ui.MainActivity;
import sandjentrance.com.sj.views.GenericViewHolder;

/**
 * Created by toidiu on 12/9/15.
 */
@Singleton
@Component(modules = {ApiModules.class, AppModule.class, ContextModule.class})
public interface ApplicationComponent {
    void inject(MainActivity obj);

    void inject(BaseActivity obj);

    void inject(BaseAction obj);

    void inject(BaseDialogFrag obj);

    void inject(BaseFullScreenDialogFrag obj);

    void inject(GenericViewHolder obj);
}
