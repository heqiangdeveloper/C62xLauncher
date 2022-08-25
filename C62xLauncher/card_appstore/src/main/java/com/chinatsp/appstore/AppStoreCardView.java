package com.chinatsp.appstore;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.appstore.bean.AppInfo;
import com.chinatsp.appstore.adapter.AppStoreAppsAdapter;
import com.chinatsp.appstore.bean.AppStoreBean;
import com.chinatsp.appstore.bean.MaterialBean;
import com.chinatsp.appstore.state.AppStoreErrorNetWorkState;
import com.chinatsp.appstore.state.AppStoreNormalState;
import com.chinatsp.appstore.state.AppStoreState;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import card.service.ICardStyleChange;
import launcher.base.network.NetworkUtils;
import launcher.base.utils.glide.GlideHelper;
import launcher.base.utils.recent.RecentAppHelper;
import launcher.base.utils.view.LayoutParamUtil;

public class AppStoreCardView extends ConstraintLayout implements ICardStyleChange, LifecycleOwner, View.OnClickListener{

    public AppStoreCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public AppStoreCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppStoreCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AppStoreCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private static final String TAG = "IQuTingCardView";
    private static final String APPSTOREPKG = "";
    private View mLargeCardView;
    private View mSmallCardView;
    private NormalSmallCardViewHolder mNormalSmallCardViewHolder;
    private NormalBigCardViewHolder mNormalBigCardViewHolder;
    private ImageView mIvAppStoreButton;
    private ImageView mIvAppIconTop;
    private ImageView mIvAppIconBottom;
    private TextView mTvAppNameTop;
    private TextView mTvAppDescTop;
    private TextView mTvAppNameBottom;
    private TextView mTvAppDescBottom;
    private AppStoreState mState;
    private boolean mExpand = false;
    private List<AppInfo> infos;
    private int mSmallWidth;
    private int mLargeWidth;

    private void init() {
        Log.d(TAG, "init");
        LayoutInflater.from(getContext()).inflate(R.layout.card_appstore, this);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        mIvAppStoreButton = (ImageView) findViewById(R.id.ivAppStoreButton);
        mIvAppIconTop = (ImageView) findViewById(R.id.ivAppIconTop);
        mIvAppIconBottom = (ImageView) findViewById(R.id.ivAppIconBottom);
        mTvAppNameTop = (TextView) findViewById(R.id.tvAppNameTop);
        mTvAppDescTop = (TextView) findViewById(R.id.tvAppDescTop);
        mTvAppNameBottom = (TextView) findViewById(R.id.tvAppNameBottom);
        mTvAppDescBottom = (TextView) findViewById(R.id.tvAppDescBottom);

        mSmallWidth = (int) getResources().getDimension(R.dimen.card_width);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);
        mIvAppStoreButton.setOnClickListener(this);
        mIvAppIconTop.setOnClickListener(this);
        mIvAppIconBottom.setOnClickListener(this);

        initApps();
        loadData();
    }

    private void loadData(){
        boolean isConnected = NetworkUtils.isNetworkAvailable(getContext());
        if (!isConnected) {
            mState = new AppStoreErrorNetWorkState();
            mState.updateViewState(AppStoreCardView.this, mExpand);
        } else {
            mState = new AppStoreNormalState();
            mState.updateViewState(AppStoreCardView.this,mExpand);

            GlideHelper.loadUrlAlbumCoverRadius(getContext(),mIvAppIconTop,infos.get(0).getIcon(),0);
            String desTop = infos.get(0).getDescription();
            if(desTop.length() > 10){
                desTop = desTop.substring(0,10) + "...";
            }
            mTvAppNameTop.setText(infos.get(0).getAppName());
            mTvAppDescTop.setText(desTop);

            GlideHelper.loadUrlAlbumCoverRadius(getContext(),mIvAppIconBottom,infos.get(1).getIcon(),0);
            String desBottom = infos.get(1).getDescription();
            if(desBottom.length() > 10){
                desBottom = desBottom.substring(0,10) + "...";
            }
            mTvAppNameBottom.setText(infos.get(1).getAppName());
            mTvAppDescBottom.setText(desBottom);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivAppStoreButton){//跳转至应用商城
            RecentAppHelper.launchApp(getContext(),APPSTOREPKG);
        }else if(v.getId() == R.id.ivAppIconTop){
            RecentAppHelper.launchApp(getContext(),APPSTOREPKG);
        }else if(v.getId() == R.id.ivAppIconBottom){
            RecentAppHelper.launchApp(getContext(),APPSTOREPKG);
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    @Override
    public void expand() {
        mExpand = true;
        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_appstore_large, this, false);
            initBigCardView(mLargeCardView);
        }

        mNormalBigCardViewHolder.updateApps(infos);
        addView(mLargeCardView);
        mState.updateViewState(this, mExpand);

        mLargeCardView.setVisibility(VISIBLE);
        mSmallCardView.setVisibility(GONE);

        LayoutParamUtil.setWidth(mLargeWidth, this);
        runExpandAnim();
    }

    private void runExpandAnim() {
        ObjectAnimator.ofFloat(mLargeCardView, "translationX", -500, 0).setDuration(150).start();
        ObjectAnimator.ofFloat(mLargeCardView, "alpha", 0.1f, 1.0f).setDuration(500).start();
    }

    private List<AppInfo> initApps(){
        String jsonStr = "{\n" +
                "    \"adInfos\": [\n" +
                "        {\n" +
                "            \"material\": {\n" +
                "                \"appInfo\": {\n" +
                "                    \"appId\": \"C105776883\",\n" +
                "                    \"appName\": \"酷狗音乐\",\n" +
                "                    \"description\": \"酷狗音乐车载版是车载平台端全新开发的定制版本，更安全的卡片设计，为广大车友提供千万正版曲库、超清无损音质、4亿人使用的蝰蛇音效，为您带来全新的音乐享受！\\n\\n海量正版曲库：千万无损音乐，就是歌多；\\n震撼蝰蛇音效：专业音效带来震撼听觉感受；\\n权威热门榜单：上亿用户为你推荐最热最新歌曲；\",\n" +
                "                    \"developerName\": \"广州酷狗计算机科技有限公司\",\n" +
                "                    \"downloadDesc\": \"23 亿次安装\",\n" +
                "                    \"downloadUrl\": \"https://appdlc-drcn.hispace.dbankcloud.cn/dl/appdl/adxopen/7xxxxxxxx\",\n" +
                "                    \"downloads\": \"2356093220\",\n" +
                "                    \"icon\": \"https://appimg.dbankcdn.com/application/icon144/e5b83c17833e47a8abb47bd6117d89b5.png\",\n" +
                "                    \"memo\": \"酷狗音乐，就是歌多\",\n" +
                "                    \"pkgName\": \"com.kugou.android.autohwbmw\",\n" +
                "                    \"releaseDate\": \"2022-04-01 17:58:05\",\n" +
                "                    \"secCategory\": \"影音娱乐\",\n" +
                "                    \"sha256\": \"5c7f04805f6830cxxxxxxxc39f8e683a4cfa4e62a31e1ba991e722b\",\n" +
                "                    \"size\": 28730982,\n" +
                "                    \"sizeDesc\": \"27.44MB\",\n" +
                "                    \"thirdCategory\": \"音乐\",\n" +
                "                    \"trackUrl\": \"https://store-drcn.hispace.dbankcloud.cn/agd/mediareport?param=xxxxxxxxxx\",\n" +
                "                    \"versionCode\": \"3032\",\n" +
                "                    \"versionName\": \"3.0.3.2\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"material\": {\n" +
                "                \"appInfo\": {\n" +
                "                    \"appId\": \"C104322615\",\n" +
                "                    \"appName\": \"芒果TV\",\n" +
                "                    \"description\": \"芒果TV是湖南广播电视台旗下唯一互联网电视平台，独家提供湖南卫视所有栏目以及芒果TV自制高清、超清视频点播和直播内容，并为用户提供各类热门电影、电视剧、综艺、动漫、音乐、娱乐、短视频等内容。\",\n" +
                "                    \"developerName\": \"湖南快乐阳光互动娱乐传媒有限公司\",\n" +
                "                    \"downloadDesc\": \"<1 万次安装\",\n" +
                "\t\t\t\t\t\"downloadUrl\": \"https://appdlc-drcn.hispace.dbankcloud.cn/dl/appdl/adxopen/7xxxxxxxx\",\n" +
                "                    \"downloads\": \"8251\",\n" +
                "                    \"icon\": \"https://appimg.dbankcdn.com/application/icon144/adc07fcf40924a3686afa99f51413e2c.png\",\n" +
                "                    \"memo\": \"密室大逃脱第三季热播中\",\n" +
                "                    \"packageName\": \"com.mgtv.tv.car\",\n" +
                "                    \"releaseDate\": \"2022-08-04 10:24:35\",\n" +
                "                    \"secCategory\": \"影音娱乐\",\n" +
                "                    \"sha256\": \"973a5f6c7c8xxxxx5bf1a80575422e6b960b49b8ecad02fb6f\",\n" +
                "                    \"size\": 25270681,\n" +
                "                    \"sizeDesc\": \"24.10MB\",\n" +
                "                    \"thirdCategory\": \"电视\",\n" +
                "                    \"trackUrl\": \"https://store-drcn.hispace.dbankcloud.cn/agd/mediareport?param=xxxxxxxxxx\",\n" +
                "                    \"versionCode\": \"102\",\n" +
                "                    \"versionName\": \"1.0.2\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"material\": {\n" +
                "                \"appInfo\": {\n" +
                "                    \"appId\": \"C101530171\",\n" +
                "                    \"appName\": \"懒人听书\",\n" +
                "                    \"description\": \"懒人听书车载版\\n听书 · 有声小说 · 电台节目 · 知识文学，4亿用户的选择！\\n涵盖全国超85%正版网络小说，16个大类，115个小类，超10万本正版有声书！ 懒人听书是一款移动有声阅读应用，提供免费听书、听电台、听新闻等有声数字收听服务，是4亿用户的选择，国内深受欢迎的有声阅读应用。\\n\\n【懒人内容】\\n涵盖全国超85%正版网络小说\\n16个大类，115个小类，超10万本正版有声书\\n\\n有声小说：庆余年、斗破苍穹、冰与火之歌、盗墓笔记、诛仙、仙逆、傲世九重天、武动乾坤、斗罗大陆、修真界败类、全职高手。\\n电台节目：简约FM、大毛二毛有话说、一米阳光、程一电台、偶遇鲁小胖、夜听、音乐留声机、一刻脱口秀。\\n相声评书：单田芳系列、刘兰芳系列、郭德纲系列、三国演义、水浒全传、岳飞传、隋唐演义、鬼吹灯、资治通鉴、嘻哈包袱铺。\\n曲艺戏曲：诸葛亮系列、京剧大观、梨园在线、卷席筒、三打金枝、七品芝麻官、舌战群儒、寇公案、茶馆、豫剧名家刘海功全集。\\n儿童：凯叔讲故事、鲁滨逊漂流记、木偶奇遇记、儿童睡前小故事、西游记少儿版、米小圈上学记、格林童话、十万个为什么、盒子历险记。\\n人文：每天读点心理学、人类简史、毛泽东最后七年、权谋曾国藩、佛度有缘人、芈氏传奇、周恩来26年、法制故事、邓小平三起三落。\\n财经：思维风暴、赢：跟韦尔奇学管理、郎咸平说、马云的人生哲学、股神是怎么炼成的、李嘉诚：一生三论、狼性生存方式、销售攻心术。\\n文学：古董局中局、人民的名义、红楼梦、金瓶梅、唐诗三百首、弹痕、千金归来、安吴商妇（周莹传 那年花开月正圆）、简·爱。\\n生活：好色男女、世界名曲、爱情心理学、20岁做黛玉，30岁学宝钗、青音入睡训练、跟小薇一起听歌、家庭医疗宝典、打造精品女人。\\n历史：世界上下五千年、历史非常档案、中国历史战争、鬼谷子的局、大清王朝、历史小说、红色足迹、中国古代史、中国近代史。\\n\\n【产品特性】\\n1.海量资源：拥有文学名著、有声小说、曲艺戏曲、名家评书、儿童文学、外语、新闻、搞笑段子、广播剧、职业技能等十几个大类海量高清有声阅读资源，每天更新发布。\\n2.上传节目：所有用户都可以上传有声节目，上传后的节目可以在懒人听书所有平台上同步收听，优质原创节目可以获得懒人的资金、包装、推广、宣传等方面的扶持。\\n3.下载收听：懒人听书上所有的书籍节目都可以免费下载收听。\\n4.交流社区：听友可以关注感兴趣的主播或有共同收听兴趣的其它听友，关注后可以获得他们的收听推荐，可以即时交流收听感受。\\n5.使用简便：界面简洁，结构有序，所有界面和操作都是为了听书而设计，没有多余的功能，老人小孩可以快速上手。\\n6.文本同步：部分作品提供文本同步阅读功能，可以打开播放器上的显示文本功能，边听边看。\\n【联系方式】\\n新浪微博：@懒人听书微博\\n微信公众号：懒人听书\\n联系邮箱：service@lazyaudio.com\",\n" +
                "                    \"developerName\": \"深圳市懒人在线科技有限公司\",\n" +
                "                    \"downloadDesc\": \"6 万次安装\",\n" +
                "                    \"downloadUrl\": \"https://appdlc-drcn.hispace.dbankcloud.cn/dl/appdl/adxopen/7xxxxxxxx\",\n" +
                "                    \"downloads\": \"63069\",\n" +
                "                    \"icon\": \"https://appimg.dbankcdn.com/application/icon144/9b607881ec894faa9a476c901a004bf3.png\",\n" +
                "                    \"memo\": \"精选热门有声小说电台故事\",\n" +
                "                    \"pkgName\": \"bubei.tingshu.hd\",\n" +
                "                    \"releaseDate\": \"2022-06-22 15:59:17\",\n" +
                "                    \"secCategory\": \"新闻阅读\",\n" +
                "                    \"sha256\": \"4edc71cd947c2xxxx770d0cab82de870840fdaa\",\n" +
                "                    \"size\": 16221470,\n" +
                "                    \"sizeDesc\": \"15.47MB\",\n" +
                "                    \"thirdCategory\": \"有声读物\",\n" +
                "                    \"trackUrl\": \"https://store-drcn.hispace.dbankcloud.cn/agd/mediareport?param=xxxxxxxxxx\",\n" +
                "                    \"versionCode\": \"46\",\n" +
                "                    \"versionName\": \"2.24.2\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"material\": {\n" +
                "                \"appInfo\": {\n" +
                "                    \"appId\": \"C101209209\",\n" +
                "                    \"appName\": \"万年历\",\n" +
                "\t\t\t\t\t\"description\": \"万年历官方出品，作为热门日历应用，我们以匠心制作了这款车载应用，在保留万年历精髓的同时，赋予其属于驾驶环境的独特体验。 \\n\\n【主要功能】 \\n提供公历、农历、天气、汽车限号等实用功能。 \\n①日历功能：提供公历农历、节日节气、放假安排等实用信息； \\n②天气功能：提供当天天气，空气质量等信息；\\n③汽车限号：提供所在城市汽车限行信息；\",\n" +
                "                    \"developerName\": \"重庆可兰达科技有限公司\",\n" +
                "                    \"downloadDesc\": \"5 亿次安装\",\n" +
                "                    \"downloadUrl\": \"https://appdlc-drcn.hispace.dbankcloud.cn/dl/appdl/adxopen/7xxxxxxxx\",\n" +
                "                    \"downloads\": \"4529848\",\n" +
                "                    \"icon\": \"https://appimg.dbankcdn.com/application/icon144/18e4814ee3974089819539056fcad607.png\",\n" +
                "                    \"memo\": \"日历天气限行应有尽有\",\n" +
                "                    \"packageName\": \"com.youloft.calendar.scar\",\n" +
                "                    \"releaseDate\": \"2022-08-18 19:16:03\",\n" +
                "                    \"secCategory\": \"实用工具\",\n" +
                "                    \"sha256\": \"c50889dcded7c6a0eacf755062f2497c82ee5839e8a681ea1941132578d72f19\",\n" +
                "                    \"size\": 109102606,\n" +
                "                    \"sizeDesc\": \"4.32MMB\",\n" +
                "                    \"thirdCategory\": \"工具\",\n" +
                "                    \"trackUrl\": \"https://store-drcn.hispace.dbankcloud.cn/agd/mediareport?param=xxxxxxxxxx\",\n" +
                "                    \"versionCode\": \"896\",\n" +
                "                    \"versionName\": \"6.0.2\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"material\": {\n" +
                "                \"appInfo\": {\n" +
                "                    \"appId\": \"C104743731\",\n" +
                "                    \"appName\": \"财联社\",\n" +
                "                    \"description\": \"财联社是由上海报业集团主管主办，持有《互联网新闻信息服务许可证》的财经通讯社和金融数据服务商，以“准确、快速、权威、专业”为新闻准则，为投资者提供专业的投资资讯。\\n【产品定位】\\n财联社通过数百名记者覆盖国内4000多家上市公司、港美股及各省百强企业的深度追踪报道能力。\\n【产品介绍】\\n拥有对券商、基金、私募、银行、保险、信托、期货、地产等多个行业深度报道能力。\\n财联社凭借强大的原创内容生产力以及深度专业分析能力，为投资者提供365天24小时不间断的电报，提供财经资讯、数据分析、专家咨询等全方位的金融信息服务，已成为机构和私募必不可少的投资工具。\",\n" +
                "                    \"developerName\": \"上海界面财联社科技股份有限公司\",\n" +
                "                    \"downloadDesc\": \"3 万次安装\",\n" +
                "                    \"downloadUrl\": \"https://appdlc-drcn.hispace.dbankcloud.cn/dl/appdl/adxopen/7xxxxxxxx\",\n" +
                "                    \"downloads\": \"33566\",\n" +
                "                    \"icon\": \"https://appimg.dbankcdn.com/application/icon144/96811b74645543e6984953531570848a.png\",\n" +
                "                    \"memo\": \"goodnotes笔记\",\n" +
                "                    \"pkgName\": \"car.com.lanjinger.choiassociatedpress\",\n" +
                "                    \"releaseDate\": \"2022-07-25 20:15:13\",\n" +
                "                    \"secCategory\": \"新闻阅读\",\n" +
                "                    \"sha256\": \"723eedd1e9exxxxxf676f6c26c3c39b97e8e98bea7af\",\n" +
                "                    \"size\": 78265712,\n" +
                "                    \"sizeDesc\": \"74.64MB\",\n" +
                "                    \"thirdCategory\": \"新闻\",\n" +
                "                    \"trackUrl\": \"https://store-drcn.hispace.dbankcloud.cn/agd/mediareport?param=xxxxxxxxxx\",\n" +
                "                    \"versionCode\": \"3\",\n" +
                "                    \"versionName\": \"2.0.1\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"material\": {\n" +
                "                \"appInfo\": {\n" +
                "                    \"appId\": \"C106294607\",\n" +
                "                    \"appName\": \"西窗烛\",\n" +
                "                    \"description\": \"西窗烛致力打造创新的中华优秀传统文化平台，提供电子书、唐诗、宋词、古文、典籍、绘画、写字、原创、朗诵、汉服、社交等古诗词学习工具和优质原创社区。西窗烛的诞生旨在建立诗词与用户、传统与现代、价值与传承的沟通桥梁，让用户可以更便捷、更全面、更自由地触达到更广阔的中华诗词以及中华优秀传统文化领域。\\n\\n获得荣誉：\\n中国获奖最多的古诗词软件。华为应用市场年度趋势应用、精选应用、精品应用、编辑精选、主打推荐、至美应用、金米奖、极光奖、设计奖、创新奖。\\n\\n\\n产品愿景：传统文化，现代传承，当人们谈到中国传统文化就能立刻想到西窗烛。西窗烛打开了一扇传统文化的窗，为大家点亮了心中的烛火，愿这烛火以星星之势，燃烧我大中华每一颗拳拳赤子之心，让更多的人认识到古代先贤的智慧与文化的魅力。西窗烛愿成为一扇「窗」，窗的一边是中华优秀传统文化，另一边是热爱中华民族的大家。\\n\\n\\n主要功能：\\n\\n【诗词文库】五千年中华文化，100万诗词文库，按朝代、作者、作品分类搜索，涵盖注释、译文和赏析。\\n\\n【精选分类】对100万诗词进行分类，包括：选集、主题、写景、节日、节气、词牌、时令、用典、地理、名山、城市、时间、花卉、小学、初中、高中等。\\n\\n【诗词作者】孔子、老子、孟子、庄子、苏轼、李白、陆游、王维、杜甫、杜牧、屈原、柳永、李煜、李商隐、辛弃疾、李清照、白居易、陶渊明、温庭筠、欧阳修、晏几道、刘禹锡、孟浩然等。 \\n\\n【经典文集】唐诗三百首、宋词三百首、元曲三百首、诗经、乐府、千家诗、古诗十九首、道德经、周易、论语、金刚经、山海经、茶经、红楼梦、三国演义、水浒传、西游记、西厢记、传习录、四时幽赏、随园食单、幽梦影、菜根谭、声律启蒙、笠翁对韵等。 \\n\\n【电子书籍】“桃李不言，下自成蹊。” 西窗烛以自信之风推开中国优秀传统文化的一扇窗。\\n\\n【学习计划】你可以设置经典诗词、文库分类和诗单收藏变成自己的学习计划，每天有计划的打卡学习。\\n\\n【一日一赏】在百万文库基础上的一日一赏和诗词卡片，指尖划过片笺片玉，诗词之美熨帖心肺。\\n\\n【卡片创作】创意的卡片式左右翻阅，卡片内容可自由创作，支持简繁和字体切换，保存浏览记录。\\n\\n【诗单、词单】像歌单一样可以创建诗单、收藏诗单，添加你喜欢的诗词到诗单，珍藏一份诗意。\\n\\n【诗词朗诵】窗外雨打芭蕉，陪你挑灯读诗。用家乡话读、陪着孩子读、学古人吟诵、配乐朗诵、为你读诗。\\n\\n【诗词抄写】一盏灯，一方印。此刻，安静的读一读诗词，此间一隅安吾心，也能觅得浮生半日闲。\\n\\n【诗情画意】一幅绘画，一首原创，一个短视频，尽情发挥你的才华。暮云春树，亦能与君共赏。\\n\\n【精选创作】每天推荐用户原创内容，包括：写字、语音、音乐、笔记、原创、新诗、配图、绘画、视频、篆刻、随笔、故事。\\n\\n【精选专辑】自己的原创作品，精选成集。各美其美，美人之美，美美与共，天下大同。\\n\\n【兴趣社交】一花一叶一追逐，一生一世一双人，寻找兴趣与共鸣。书卷多情似故人，晨昏忧乐每相亲。\\n\\n【练字模式】摘录页、诗词页可以切换到练字模式，体验临摹、练字的场景，提供丰富的字体和样式。\\n\\n【丰富字体】提供楷书、行书、篆书、隶书、草书、刻本、毛笔、宋体、仿宋、创意等几十种漂亮唯美的字体。\\n\\n【兴趣话题】李白、苏轼及爱情、秋日等都是话题。各抒胸臆感受思想碰撞的火花，素未谋面却能倾盖如故。\\n\\n【诗词游戏】诗词大会、飞花令和诗词接龙，累计有百万人次参加，诗词在富有趣味性和竞技性的游戏过程中焕发新的魅力。\\n\\n【文创市集】文艺美学，精致生活。打造具有东方美学元素的文创市集平台。\\n\\n【西窗时间】优质音频内容课程，阐幽发微的解读让传统文化更加普及。当一棵树摇动另一棵树，一朵云推动另一朵云，隔着千年与另一个有趣的灵魂相比时，才是真正的教育。\\n\\n\\n自动续费：\\n西窗烛提供了不同的自动续订选项，具体选项请查看会员中心。\\n- 订阅会自动续订，除非在本期结束前24小时关闭自动续费。\\n- 在本期结束前的24小时内，将向你的华为账号收费费用。\\n- 你可以在华为应用市场账户设置中关闭和管理自动续订功能。具体操作： “账号中心” --> “付款与账单” --> “自动续费/免密支付” --> “订阅” 处查看订阅服务。\\n\\n\\n\\n【联系我们】\\n官方微博号：西窗烛\\n微信小程序：西窗烛\\n微信公众号：西窗烛\\n联系邮箱：hi@xcz.im\\n官方QQ群：133575768\",\n" +
                "                    \"developerName\": \"北京西窗文化传媒有限公司\",\n" +
                "                    \"downloadDesc\": \"5,356 万次安装\",\n" +
                "                    \"downloadUrl\": \"https://appdlc-drcn.hispace.dbankcloud.cn/dl/appdl/adxopen/7xxxxxxxx\",\n" +
                "                    \"downloads\": \"53569289\",\n" +
                "                    \"icon\": \"https://appimg.dbankcdn.com/application/icon144/43b619acc6b54192b45b6389aa8923a4.png\",\n" +
                "                    \"memo\": \"赏中华诗词，品生活之美；传统文化，现代传承。\",\n" +
                "                    \"pkgName\": \"com.xcz.car.hw\",\n" +
                "                    \"releaseDate\": \"2022-07-20 18:10:44\",\n" +
                "                    \"secCategory\": \"教育\",\n" +
                "                    \"sha256\": \"f7a6e5c45xxxx2f0d41ddf3532e41623387c0\",\n" +
                "                    \"size\": 58961428,\n" +
                "                    \"sizeDesc\": \"56.23MB\",\n" +
                "                    \"thirdCategory\": \"学习\",\n" +
                "                    \"trackUrl\": \"https://store-drcn.hispace.dbankcloud.cn/agd/mediareport?param=xxxxxxxxxx\",\n" +
                "                    \"versionCode\": \"192\",\n" +
                "                    \"versionName\": \"7.5.6\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"requestId\": \"8543babcea534fc39a6aab0b9bea79\",\n" +
                "    \"rtnCode\": 0,\n" +
                "    \"rtnDesc\": \"success\"\n" +
                "}";
        Gson gson = new Gson();
        AppStoreBean appStoreBean = gson.fromJson(jsonStr, AppStoreBean.class);
        List<MaterialBean> materialBeanList = appStoreBean.getAdInfos();
        infos = new ArrayList<>();
        for(MaterialBean materialBean : materialBeanList){
            infos.add(materialBean.getMaterial().getAppInfo());
        }
        return infos;
    }

    @Override
    public void collapse() {
        mExpand = false;

        mSmallCardView.setVisibility(VISIBLE);
        mLargeCardView.setVisibility(GONE);
        removeView(mLargeCardView);
        LayoutParamUtil.setWidth(mSmallWidth, this);
    }

    private void initBigCardView(View largeCardView) {
        mNormalBigCardViewHolder = new NormalBigCardViewHolder(mLargeCardView);
        RecyclerView rcvCardIQuTingSongList = largeCardView.findViewById(R.id.rcvAppStoreAppsList);
        //rcvCardIQuTingSongList.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rcvCardIQuTingSongList.setLayoutManager(layoutManager);
        // 间隔24px
//        SimpleRcvDecoration divider = new SimpleRcvDecoration(24,layoutManager );
//        if (rcvCardIQuTingSongList.getItemDecorationCount() <= 0) {
//            rcvCardIQuTingSongList.addItemDecoration(divider);
//        }
        AppStoreAppsAdapter adapter = new AppStoreAppsAdapter(getContext());
        rcvCardIQuTingSongList.setAdapter(adapter);
        rcvCardIQuTingSongList.getItemAnimator().setChangeDuration(0); //防止recyclerView刷新闪屏

        mNormalBigCardViewHolder.setAppsAdapter(adapter);
    }

    @Override
    public boolean hideDefaultTitle() {
        return false;
    }

    private class NormalSmallCardViewHolder {
        NormalSmallCardViewHolder() {

        }

        void updateMediaInfo() {

        }
    }

    private class NormalBigCardViewHolder {
        private AppStoreAppsAdapter mAppStoreAppsAdapter;
        NormalBigCardViewHolder(View largeCardView) {
            updateCover();
        }

        public void setAppsAdapter(AppStoreAppsAdapter adapter) {
            mAppStoreAppsAdapter = adapter;
        }

        public void updateApps(List<AppInfo> infos) {
            mAppStoreAppsAdapter.setData(infos);
        }

        public void updateCover() {
            //GlideHelper.loadLocalCircleImage(getContext(), mIvIQuTingCoverBig, R.drawable.test_cover2);
        }
    }
}
