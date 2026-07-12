Android开发技术项目报告

BiteLite（DashDine）极简外卖点餐App


学生信息

姓名：（请填写）
学号：（请填写）
班级：（请填写）
项目名称：BiteLite（DashDine）极简点餐 App
开发环境：Android Studio + Kotlin + Jetpack Compose
完成日期：2026 年 6 月


第1章 前言

1.1 项目目的

本项目旨在设计并实现一款基于 Android 平台的极简外卖点餐应用——BiteLite（DashDine）。随着移动互联网的深度普及，外卖点餐已成为人们日常生活中的高频刚需。然而，当前主流外卖 App 普遍存在功能臃肿、界面繁杂、广告干扰严重等问题，用户体验逐渐下降。

基于此背景，本项目提出"小而美"的设计理念，聚焦于点餐核心流程，剔除冗余功能，力求在提供完整点餐能力的同时，保持界面简洁、交互流畅、视觉现代。通过本项目的开发，实践 Kotlin 语言、Jetpack Compose 声明式 UI 框架、MVVM 架构模式、Hilt 依赖注入等现代 Android 开发技术，构建一个架构清晰、代码高内聚低耦合、具备良好可扩展性的移动端应用。

1.2 项目总概

BiteLite 是一款完整的外卖点餐 Android 应用，覆盖"浏览店铺 → 选择菜品 → 加入购物车 → 确认订单 → 支付 → 查看历史订单"的端到端业务流程。项目采用 Kotlin 作为开发语言，Jetpack Compose 构建声明式 UI，MVVM（Model-View-ViewModel）结合 UDF（单向数据流）作为架构范式，通过 Hilt 实现依赖注入，利用 Coroutines + Flow 处理异步数据流，使用 Coil 加载网络图片，集成 ZXing 库本地生成支付二维码。

项目共包含 7 个核心页面、30 余个 Kotlin 源文件、40 余个 Composable 可复用组件。数据层通过 Repository 接口抽象，当前采用 Mock 假数据实现（模拟 150-300ms 网络延迟），预留了未来接入真实 Retrofit 后端的扩展能力。全局购物车状态通过 CartManager 单例跨页面共享，基于 StateFlow 实现响应式状态管理，确保多页面间的数据一致性。


第2章 需求分析

2.1 项目背景

2.1.1 行业背景

据艾瑞咨询数据，中国外卖市场规模已突破万亿，用户规模超过 5 亿。外卖 App 作为连接消费者与餐饮商家的核心桥梁，其用户体验直接影响消费决策和复购率。然而，现有头部外卖平台（如美团、饿了么）在功能日益丰富的同时，也面临着界面信息过载、操作路径冗长、广告推送频繁等问题。部分用户——尤其是中老年群体和追求效率的年轻白领——对一款界面清爽、操作直观、专注核心功能的轻量级点餐工具有着真实需求。

2.1.2 技术背景

Android 开发技术栈近年来经历了重大变革。Google 于 2021 年正式发布 Jetpack Compose，标志着 Android UI 开发从传统的 XML + View 体系向声明式 UI 范式的全面转型。Compose 通过可组合函数（Composable）描述 UI，无需手动管理 View 生命周期，显著减少了样板代码量，提升了开发效率。与此同时，Kotlin 协程（Coroutines）和 Flow 的引入，为异步编程和响应式数据流提供了更优雅的解决方案。Hilt 作为基于 Dagger 的简化依赖注入框架，大幅降低了 DI 的使用门槛。

本项目充分拥抱上述现代 Android 技术栈，旨在构建一个架构规范、代码可读性强、便于后续迭代维护的参考级应用。

2.2 需求分析

2.2.1 功能需求

经过对外卖点餐典型用户旅程的分析，本项目梳理出以下核心功能模块：

（1）首页与店铺浏览
- 展示当前定位信息（默认"宜宾翠屏区"），提供定位切换入口
- 提供全局搜索栏，支持按店铺名称和标签关键词实时过滤
- 金刚区展示 8 个快捷分类入口（外卖、到店、甜品、咖啡、面食、快餐、饮品、烘焙）
- 以垂直列表展示附近店铺卡片，每张卡片包含店铺 Logo、名称、评分（⭐）、月销量、起送价、配送费、配送时间、活动标签
- 提供订单历史查看入口

（2）店铺详情与菜单浏览
- 展示店铺封面大图、基本信息（名称、评分、月售、起送价、配送费）
- 实现左侧分类导航 + 右侧菜品列表的双联动滚动布局
- 分类包含：热销、折扣、主食、小食、饮品、甜品等 6 个类别
- 菜品卡片展示菜品图片、名称、描述摘要、月售、价格、原价（划线）、+/- 数量步进器
- 底部悬浮购物车条：实时显示已选商品总数量和总价
- 点击购物车条弹出 BottomSheet 购物车面板，支持修改数量和清空

（3）菜品详情
- 顶部 320dp 沉浸式高清菜品大图，图片底部渐变遮罩自然过渡到白色信息区
- 加载中/加载失败分别展示对应占位状态
- 菜品名称、详细描述（含口味说明、食材产地等）
- 信息标签行：热量（🔥）、过敏原（⚠️）、月售（📦）、评分（⭐）
- 可选规格选择：辣度（不辣/微辣/中辣/特辣）、甜度（无糖/半糖/全糖）、温度（常温/去冰/加冰），以 Material 3 FilterChip 单选组实现
- 食材信息展示
- 底部操作栏：收藏按钮 + 当前价格（含划线原价）+ "加入购物车"按钮（点击触发弹性缩放动画，文案变为"已加N份"）

（4）购物车管理
- 全局购物车单例（CartManager），跨页面共享状态
- 支持多店铺隔离：切换店铺时自动清空购物车并提示
- 购物车 BottomSheet 以 Dialog 半屏面板形式呈现
- 支持在购物车中直接修改数量（+/- 步进器）和一键清空

（5）确认订单
- 收货地址卡片：地图图标 + 姓名 + 电话 + 详细地址 + 右箭头
- 点击地址卡片弹出 AlertDialog，支持编辑收货人、手机号、详细地址
- 订单明细：菜品缩略图 + 菜品名 + 单价小计 + Stepper 步进器（支持确认页直接修改数量）
- 费用明细：商品小计 + 配送费（5元）+ 包装费（2元）+ 满减优惠（满30元减5元，红色高亮）
- 修改数量后所有费用自动实时重算
- 备注栏：快速备注标签（不辣、少辣、多放葱、不要香菜、少盐、多加醋，支持多选）+ 自定义文本输入框
- 底部支付栏：醒目展示"实付款"金额 + "立即支付"珊瑚橘主按钮
- 购物车为空时支付按钮自动禁用

（6）支付与订单
- 支付页面展示订单金额和订单号
- 支付方式切换：微信支付 / 支付宝 Tab（选中态有颜色区分）
- 使用 ZXing 库在客户端本地生成支付二维码（编码目标 URL），260dp 圆角卡片展示
- "已完成支付"按钮模拟支付完成，点击后导航至支付成功页
- 支付成功页展示弹性缩放绿色对勾动画 + 扩散圆动画 + 订单号

（7）历史订单
- 订单列表按创建时间倒序排列
- 每张订单卡片展示：订单号、订单状态（颜色标签区分：待支付/准备中/配送中/已完成/已取消）、菜品摘要、下单时间、实付金额
- 空状态展示友好提示

2.2.2 非功能需求

（1）性能要求
- 应用冷启动时间不超过 2 秒
- 页面切换动画帧率不低于 60fps
- 列表滚动无卡顿，支持阻尼回弹效果
- 图片加载采用 Coil 内存缓存 + 磁盘缓存策略

（2）兼容性要求
- 最低支持 Android 7.0（API 24），覆盖 95% 以上活跃设备
- 目标 SDK 为 Android 14（API 35）
- 适配不同屏幕尺寸（手机）和分辨率

（3）代码质量要求
- 严格遵循 MVVM 架构：ViewModel 不持有任何 UI 引用
- UI 组件高度抽离为独立 Composable 函数，单文件代码不过长
- 数据层通过 Repository 接口抽象，面向接口编程
- 所有异步操作使用 Kotlin Coroutines，避免回调地狱
- 状态管理使用 StateFlow + UDF（单向数据流），确保数据流向可追踪

（4）用户体验要求
- UI 风格极简现代：大面积留白、低饱和度色彩、大圆角卡片（16-24dp）、柔和阴影
- 主色调仅用于核心 CTA 按钮，避免色彩滥用
- 关键操作提供动画反馈（加购弹动、支付成功动画）
- 异常状态均有对应 UI 反馈（加载中、加载失败、空数据）


第3章 内容与方法

3.1 设计要求

3.1.1 技术选型原则

本项目技术选型遵循以下原则：优先采用 Google 官方推荐的现代 Android 技术栈；选择社区活跃、文档完善的第三方库；技术组合需保证架构一致性，避免不同范式混用带来的认知负担和学习成本。

3.1.2 具体技术方案

（1）开发语言：Kotlin 2.1.10
Kotlin 作为 Android 官方推荐的一级开发语言，具备空安全、扩展函数、协程、数据类等现代语言特性，显著减少 NullPointerException 和样板代码。相对于 Java，Kotlin 的语法更简洁，与 Compose 的 DSL 风格天然契合。

（2）UI 框架：Jetpack Compose + Material 3
Compose 采用声明式编程范式，通过 Composable 函数描述 UI 的外观和行为。相比传统 XML + View 体系，Compose 无需手动管理 View 生命周期和状态同步，UI 自动随状态变化而重组（Recomposition）。Material 3 提供了最新的 Material Design 设计规范组件（如 FilterChip、TopAppBar、BottomAppBar 等），与 Compose 深度集成。本项目使用 Compose BOM 2024.12.01 版本统一管理依赖版本。

（3）架构模式：MVVM + UDF（单向数据流）
Model（数据模型）：纯数据类（data class），不包含业务逻辑。
View（UI 层）：Composable 函数，仅负责渲染 UI 和转发用户事件，通过 collectAsState() 订阅 ViewModel 的 StateFlow 暴露的只读状态。
ViewModel（业务逻辑层）：持有 MutableStateFlow<UiState>，对外暴露只读的 StateFlow。所有业务逻辑（数据加载、状态变更、计算）在 ViewModel 中完成。ViewModel 构造函数通过 Hilt 注入 Repository 等依赖。

单向数据流保证了：
- 状态变化可预测、可追溯
- UI 只是状态的纯函数：UiState → UI
- 避免了双向绑定带来的状态同步混乱

（4）依赖注入：Hilt 2.53.1
Hilt 是 Google 基于 Dagger 封装的 Android 专用依赖注入框架，通过注解（@HiltAndroidApp、@HiltViewModel、@Inject、@Module、@Binds）自动生成 DI 代码，大幅降低 Dagger 的配置复杂度。本项目使用 Hilt 管理 Repository 绑定、CartManager 单例提供、ViewModel 的依赖注入。

（5）异步处理：Kotlin Coroutines 1.9.0 + Flow
协程（Coroutines）：用于 ViewModel 中所有异步操作（数据加载、订单提交），使用 viewModelScope 自动管理协程生命周期，避免内存泄漏。
Flow：用于响应式数据流。ViewModel 通过 MutableStateFlow 管理状态，UI 通过 collectAsState() 自动订阅；CartManager 通过 StateFlow 暴露购物车数据，实现跨页面自动同步。

（6）导航：Navigation Compose 2.8.8
Jetpack Navigation Compose 为 Compose 提供了类型安全的路由导航。本项目使用密封类 Screen 定义路由表，通过 NavHost + composable() 构建导航图，支持路径参数（如 storeId、dishId、orderId）传递，并通过 popUpTo 管理返回栈。

（7）图片加载：Coil 2.6.0
Coil 是专为 Kotlin 设计的轻量级图片加载库，底层支持 OkHttp、内存缓存、磁盘缓存、Bitmap 池复用。与 Compose 深度集成，提供 AsyncImage 和 SubcomposeAsyncImage 两个 Composable，后者支持自定义 loading/error 状态。

（8）二维码生成：ZXing 3.5.3
ZXing（Zebra Crossing）是 Google 开源的条码图像处理库，支持多种条码格式的编码和解码。本项目使用其 QRCodeWriter 在客户端本地生成二维码 Bitmap，无需依赖网络 API。

3.1.3 UI 设计规范

（1）色彩系统
主色调：珊瑚橘 #FF7F50（CoralOrange），仅用于核心 CTA 按钮（"加入购物车"、"立即支付"），避免滥用保持视觉焦点。
辅助色：薄荷绿 #20B2AA（MintGreen）。
背景色：大面积留白 #F8F9FA（BackgroundWhite），卡片表面纯白 #FFFFFF（SurfaceWhite）。
文字色：主文字 #1A1A2E，辅助文字 #8A8A8A，占位提示 #BDBDBD。
功能色：绿色 #4CAF50（成功/免配送费），红色 #E53935（折扣/满减），橙色 #FF9800（评分/待支付），蓝色 #2196F3（配送中）。

（2）排版规范
全局使用无衬线字体（SansSerif），字号体系分为：大标题 28sp（Bold）、区域标题 22sp（Bold）、小标题 18sp（SemiBold）、正文大 16sp、正文 14sp、说明文字 12sp。字重分明，大标题使用粗体，辅助文字使用常规体，确保信息层级清晰。

（3）圆角与阴影
卡片圆角范围为 16dp-24dp，营造柔和友好的视觉感受。按钮采用 24dp 胶囊形圆角。Chip 标签采用 20dp 圆角。卡片阴影使用 elevation 2dp 配合 shadow(8dp)，产生"悬浮"的轻盈感。底部购物车条使用 shadow(12dp) 强化浮层效果。

（4）动画规范
加入购物车：animateFloatAsState 驱动 scale 值从 1f 到 1.3f，spring 弹性动画（dampingRatio=0.3f, stiffness=400f），产生按压弹动反馈。
支付成功对勾：Animatable 驱动从 0f 到 1f 的缩放，spring 弹性动画（dampingRatio=0.4f, stiffness=300f）。
支付成功背景圆：animateFloatAsState 驱动从 0f 到 1f 的扩散，tween 线性动画（duration=800ms）。

3.2 设计步骤

3.2.1 第一阶段：项目初始化与技术选型

（1）使用 Android Studio 创建 Kotlin + Jetpack Compose 项目，设置 Minimum SDK 24、Target SDK 35。
（2）配置 Gradle 版本目录（libs.versions.toml），统一管理 Compose BOM、Hilt、Coil、Navigation、Coroutines 等依赖版本。
（3）启用 KSP（Kotlin Symbol Processing）替代 KAPT，提升注解处理速度。
（4）创建 BiteLiteApp 类（继承 Application），标注 @HiltAndroidApp 作为 Hilt 依赖注入入口。
（5）搭建基础包结构：data/model、data/mock、data/repository、di、navigation、ui/theme、ui/components、ui/home、ui/store、ui/dish、ui/cart、ui/checkout、ui/payment、ui/order。

3.2.2 第二阶段：数据层构建（对应开发手册 15.3）

（1）定义数据实体类（Kotlin data class）：
- Store（店铺）：id、name、logoUrl、coverUrl、rating、monthlySales、minOrderPrice、deliveryFee、deliveryTime、tags、distance、isNew、hasDiscount。
- Category（分类）：id、name、storeId、dishCount。
- Dish（菜品）：id、name、imageUrl、description、price、originalPrice、monthlySales、rating、categoryId、storeId、specs、allergens、calories、ingredients。
- SpecGroup（规格组）和 SpecOption（规格选项）：name、options / name、priceDelta。
- CartItem（购物车项）：dish、quantity、selectedSpecs，totalPrice 为计算属性（(单价+规格加价)×数量）。
- Order（订单）：id、storeId、items、totalAmount、deliveryFee、packingFee、discount、status（枚举：PENDING_PAYMENT、PREPARING、DELIVERING、COMPLETED、CANCELLED）、address、phone、remark、createTime，finalAmount 为计算属性。

（2）定义 AppRepository 接口，声明所有数据访问方法为 suspend 函数：
getStores()、getStoreById(storeId)、getCategoriesByStore(storeId)、getDishesByCategory(categoryId)、getDishById(dishId)、createOrder(order)、getOrders()、getOrderById(orderId)。

（3）编写 MockData 假数据类，提供 5 家店铺（老王家的味道、川味小馆、轻食沙拉实验室、深夜烧烤屋、和风日料），每家店铺 6 个分类（热销、折扣、主食、小食、饮品、甜品），共计 32+ 道菜品，覆盖不同价格区间和规格类型。菜品配有来自 Unsplash 的高质量食物图片 URL。

（4）实现 MockAppRepository，每个数据访问方法内部使用 delay(150-300ms) 模拟网络延迟，从 MockData 中查询并返回数据。

（5）编写 Hilt AppModule，使用 @Binds 将 MockAppRepository 绑定到 AppRepository 接口，作用域为 @Singleton。

3.2.3 第三阶段：首页实现（对应开发手册 15.4）

（1）创建 HomeViewModel：
- 定义 HomeUiState 数据类（stores: List<Store>、isLoading: Boolean、searchQuery: String、location: String）
- init 中调用 repository.getStores() 加载店铺列表
- 提供 onSearchQueryChange() 方法更新搜索关键词
- 提供 getFilteredStores() 方法，根据搜索关键词过滤店铺（匹配店名和标签）

（2）创建 HomeScreen Composable：
- 使用 Scaffold 构建页面骨架
- HomeHeader 组件：Column 布局，渐变背景（CoralOrange → 透明），包含定位行（📍 + "宜宾翠屏区" + 下拉箭头 + 订单按钮 + 通知图标）和搜索栏（AppSearchBar，圆角 + 放大镜图标）。
- KingKongSection 组件：LazyRow 横向排列 8 个分类图标，每个图标带彩色圆角背景（16dp 圆角）。
- 店铺列表区：标题"附近好店" + LazyColumn + StoreCard 组件。
- 加载中显示 CircularProgressIndicator。

（3）创建 StoreCard Composable：
- Card 圆角 20dp，shadow 8dp
- Row 布局：左侧 80dp 方形店铺 Logo（AsyncImage），新店右下角覆盖"新店"标签
- 右侧 Column：店名（titleMedium 粗体）、评分星标行（⭐ + 评分 + 月售 + 距离）、起送价与配送费行（免配送费标绿色）、标签行（满减蓝色、新店橘色、免费绿色）

3.2.4 第四阶段：店铺详情实现（对应开发手册 15.5）

（1）创建 StoreDetailViewModel：
- 定义 StoreDetailUiState（store、categories、dishesByCategory、selectedCategoryIndex、isLoading）
- 加载店铺数据、分类列表，遍历每个分类加载对应菜品
- 提供 onCategorySelected(index) 切换选中分类
- 提供 onAddDish(dish) 和 onRemoveDish(dishId) 操作 CartManager
- 通过 viewModelScope.launch 订阅 cartManager.items Flow，实时更新购物车总数量和总价

（2）创建 StoreDetailScreen Composable：
- TopAppBar 展示店铺名称 + 返回按钮
- 封面图区：180dp AsyncImage + 底部渐变遮罩（Transparent → Black 35% 透明度）
- 店铺信息卡片：白色圆角面板，展示店名（headlineSmall 粗体）、评分、月售、起送价、配送费、配送时间
- 双联动区域（Row，占满剩余空间 weight(1f)）：
  左侧分类导航（90dp）：LazyColumn，选中项珊瑚橘左侧指示条（3dp 宽）+ 加粗文字 + 纯白背景
  右侧菜品列表（weight(1f)）：LazyColumn，按选中分类过滤菜品，使用 DishCard 组件
- 底部悬浮购物车条：条件渲染（cartTotalQuantity > 0），暗色背景（#2D2D2D）、圆角 28dp、shadow 12dp。显示购物车图标 + 数量角标 + 总价 + "去结算"按钮

（3）创建 DishCard Composable：
- Row 布局，整行可点击（clickable）
- 左侧 90dp 菜品图（AsyncImage，12dp 圆角）
- 右侧 Column：菜品名（bodyLarge 粗体，单行截断）、描述（40 字截断）、月售、价格行（¥ + 当前价格 + 划线原价 + Stepper 步进器）

（4）创建 Stepper 组件：
- 数量为 0 时显示圆形橙色 + 按钮（28dp）
- 数量大于 0 时显示 - 数量 + 三段式（减号 + 数量文字 + 橙色圆形加号）
- + 和 - 均有点击动画反馈

（5）创建 CartManager 单例：
- 使用 MutableStateFlow<List<CartItem>> 管理购物车列表
- addDish(dish, storeId)：如果 storeId 与当前店铺不同则清空购物车；已存在菜品加 1，否则新增
- removeDish(dishId)：数量大于 1 则减 1，否则移除；购物车为空时清空店铺 ID
- clearCart()：清空购物车
- getQuantity(dishId)：查询某菜品数量
- getCartItems()、getTotalQuantity()、getTotalPrice() 辅助方法

（6）创建 CartSheet Composable：
- 使用 Dialog + usePlatformDefaultWidth = false 实现自定义尺寸面板
- 顶部 60% 屏幕高度，圆角 24dp
- 标题行："购物车" + "清空"按钮
- LazyColumn 列出 CartItemRow（图片 + 菜品名 + 单价 + Stepper）
- 底部结算栏：总价 + 共 N 件商品 + "去结算"按钮
- 通过 Hilt EntryPoint（CartEntryPoint）获取 CartManager 实例

3.2.5 第五阶段：菜品详情实现（对应开发手册 15.6）

（1）创建 DishDetailViewModel：
- 从 SavedStateHandle 获取 dishId
- loadDish() 协程方法：调用 repository.getDishById(dishId)，若为 null 则设置 loadError 状态；否则初始化默认规格选择（每个规格组第一个选项）和当前购物车数量
- onSpecSelected(groupName, optionName)：更新规格选中状态
- onAddToCart()：调用 cartManager.addDish() 并更新 UI 中的购物车数量

（2）创建 DishDetailScreen Composable：
- TopAppBar：显示菜品名称 + 返回按钮
- 内容区三态处理：isLoading（加载动画）→ loadError（错误提示含 dishId）→ 正常内容
- 沉浸式大图：320dp Box，SubcomposeAsyncImage（loading 占位：灰色图标 + "加载中..."；error 占位：灰色图标 + 菜品名 + "图片加载失败"），底部渐变遮罩过渡
- 信息面板（20dp 水平内边距）：菜品名称（headlineMedium 粗体）、详细描述（bodyMedium）、信息标签行（InfoChip：🔥热量、⚠️过敏原、📦月售、⭐评分）
- 规格选择（条件渲染）：只有当 dish.specs 不为空时才展示，分组标题 + FilterChip 单选组（选中态珊瑚橘浅背景 + 珊瑚橘文字）
- 食材信息（条件渲染）：分隔线 + "食材"标题 + 食材文本
- BottomAppBar 底部操作栏：收藏按钮（IconButton）+ 价格区（¥当前价 + 划线原价）+ "加入购物车"按钮（CoroutineOrange，弹性动画 scale）

3.2.6 第六阶段：订单流程实现（对应开发手册 15.7）

（1）创建 CheckoutViewModel：
- 初始化时从 CartManager 读取购物车数据，计算商品小计、配送费（5元）、包装费（2元）、满减优惠（满30减5元）、实付款
- 地址编辑：onAddressChange(name, phone, address) 更新收货信息
- 数量修改：onIncreaseDish(dishId)、onDecreaseDish(dishId)，修改后调用 recalculateTotals() 实时重算所有费用
- 备注管理：onRemarkChange() 文本备注、onQuickRemarkClick() 快速标签（支持多选切换）
- 订单提交：submitOrder() 构建 Order 对象 → repository.createOrder() → cartManager.clearCart() → 回调 onSuccess(orderId, finalAmount)

（2）创建 CheckoutScreen Composable：
- TopAppBar："确认订单" + 返回按钮
- LazyColumn 内容区：
  · 收货地址卡片：Card + clickable（点击弹出 AlertDialog 编辑姓名/电话/地址），地图图标 + 姓名粗体 + 手机 + 地址 + 右箭头
  · 订单明细卡片：标题"订单明细" + 菜品列表（图片 48dp + 菜品名 + 单价小计 + Stepper 步进器）+ 费用明细行（商品小计、配送费、包装费、满减优惠红色显示）+ 空购物车提示
  · 备注卡片：快速备注标签（Surface + clickable，选中态珊瑚橘边框和浅色背景）+ OutlinedTextField 自定义备注
- 底部支付栏：Surface + shadow(16dp)，显示"实付款" + 金额（CoralOrange 大字体）+ "立即支付"按钮（购物车为空时禁用，提交时显示 loading 动画）

（3）创建 PaymentScreen：
- TopAppBar："支付订单" + 返回按钮
- 订单金额醒目展示（40sp CoralOrange）
- 支付方式切换：微信支付 / 支付宝 Tab（Weight 均分，选中态珊瑚橘边框 + 浅色背景）
- 二维码卡片：260dp 白色圆角卡片，调用 generateQrCode("https://github.com/Zzzzzzy3", 512) 生成 Bitmap 并显示
- ZXing 生成逻辑：QRCodeWriter 将 URL 编码为 BitMatrix（纠错级别 M、UTF-8 编码），遍历矩阵像素生成 ARGB_8888 Bitmap
- "已完成支付"按钮 + "点击按钮模拟支付成功"提示

（4）创建 OrderSuccessScreen：
- 居中布局 + 支付成功动画
- 绿色对勾：CircleShape Surface + Icons.Default.Check + Animatable 缩放弹性动画
- 背景扩散圆：100dp Box + scale 动画
- "支付成功！"大标题 + "您的订单已提交，商家正在准备中" + 订单号
- "返回首页"主按钮 + "查看订单详情"文字按钮

（5）创建订单历史功能：
- OrderListViewModel：加载 repository.getOrders()，按 createTime 倒序排列
- OrderListScreen：LazyColumn 展示 OrderCard 列表
- OrderCard：订单号 + 状态标签（颜色区分 5 种状态）+ 菜品摘要（最多 3 个、超出显示"等N件"）+ 下单时间（yyyy-MM-dd HH:mm）+ 实付金额
- 空状态：Receipt 图标 + "暂无订单" + 提示文案
- 首页顶部 Header 新增订单图标按钮（Icons.AutoMirrored.Filled.ReceiptLong）作为入口

3.2.7 第七阶段：导航与页面流转

（1）定义 Screen 密封类，声明 7 个路由：
Home → StoreDetail（携带 storeId）→ DishDetail（携带 dishId）
StoreDetail → Checkout（携带 storeId）→ Payment（携带 orderId + orderAmount）→ OrderSuccess（携带 orderId）
Home → OrderList

（2）在 NavGraph 中配置 composable() 路由：
- 路径参数使用 navArgument 声明类型（NavType.StringType）
- Checkout → Payment 使用 popUpTo(Screen.Home.route) 清除中间页
- Payment → OrderSuccess 使用 popUpTo(Screen.Payment.route, inclusive=true) 移除支付页
- OrderSuccess → Home 使用 popBackStack(Screen.Home.route, inclusive=false)

3.2.8 第八阶段：主题与 UI 组件打磨

（1）创建 Color.kt：定义 16 种语义化颜色常量。
（2）创建 Type.kt：定义 Material 3 Typography 8 级字体样式。
（3）创建 Theme.kt：组装 lightColorScheme + AppTypography，设置状态栏为浅色模式（isAppearanceLightStatusBars = true），通过 SideEffect 确保 Compose 重组时正确应用。
（4）提取可复用组件：Stepper（步进器）、DishCard（菜品卡片）、StoreCard（店铺卡片）、AppSearchBar（搜索栏）、InfoChip（信息标签）、FeeRow（费用行）。
（5）为所有列表项指定稳定的 key（如 dish.id、store.id、order.id），确保 Compose 高效重组。


第4章 技术难点与解决方案

4.1 Kotlin 字符串模板中属性访问的解析陷阱

问题描述：在菜品详情页的信息标签行中，使用 "🔥 $dish.calories" 格式的字符串模板，预期显示 "🔥 650kcal"，实际却显示为 "🔥 Dish(id=s1_d2, name=香辣鸡腿堡套餐, price=28.0, ...).calories" 的乱码。

问题分析：Kotlin 字符串模板解析器在处理 $variableName.identifier 格式时，默认将 $variableName 作为一个完整的模板表达式进行求值（即调用 dish.toString()），而将紧随其后的 .identifier 视为普通文本字面量。正确的属性访问写法必须使用花括号明确模板表达式的边界。

解决方案：将所有 $variableName.propertyName 形式的字符串模板修改为 ${variableName.propertyName} 形式。例如 "🔥 ${dish.calories}"、"⚠️ ${dish.allergens}"。同理，"月售${dish.monthlySales}+" 和 "⭐ ${dish.rating}" 也使用了正确的花括号写法。此问题体现了 Kotlin 字符串模板语法的常见陷阱，在访问对象属性时应始终使用 ${} 形式以避免歧义。

4.2 跨页面购物车状态同步

问题描述：用户在店铺详情页添加菜品到购物车后，进入确认订单页需要看到最新的购物车数据；在确认订单页修改数量后，返回店铺详情页也需要看到同步的数据变化。传统的 Activity/Fragment 间通过 Intent 或回调传递数据的方案在 Compose 单 Activity 多页面场景下不够优雅。

解决方案：引入 CartManager 单例（@Singleton 作用域），内部使用 MutableStateFlow<List<CartItem>> 管理购物车状态。所有需要读写购物车的 ViewModel 通过构造函数注入同一个 CartManager 实例。UI 层通过 collectAsState() 订阅 Flow 的只读暴露，当任意页面修改购物车时，所有订阅方自动收到最新的数据并触发 UI 重组。此方案实现了"一处修改，处处同步"的响应式数据共享。

4.3 确认订单页的实时费用重算

问题描述：用户在确认订单页使用 Stepper 修改菜品数量时，需要实时更新商品小计、满减优惠和实付款。这些费用之间存在计算依赖：满减优惠依赖商品小计（满 30 减 5），实付款 = 小计 + 配送费 + 包装费 - 优惠。如果每次修改都重复编写计算逻辑，代码会变得冗余且容易遗漏。

解决方案：在 CheckoutViewModel 中抽取私有的 recalculateTotals() 方法，统一处理所有费用的计算逻辑。onIncreaseDish() 和 onDecreaseDish() 在调用 CartManager 修改数量后，统一调用 recalculateTotals() 一次性更新 UiState 中的 totalAmount、discount、finalAmount 等字段。此方案将计算逻辑集中在单一方法中，确保费用计算的一致性和可维护性。

4.4 支付二维码的纯客户端生成

问题描述：支付页面需要展示二维码图片。常见的做法有两种：调用在线 API 生成二维码图片返回 URL，或者使用第三方 SDK。前者依赖网络且可能产生费用，后者引入额外依赖。

解决方案：使用 Google 开源的 ZXing（Zebra Crossing）库在客户端本地生成二维码。核心技术流程：
（1）创建 QRCodeWriter 实例，配置编码提示（EncodeHintType：UTF-8 字符集、M 级纠错、边距 1）。
（2）调用 writer.encode(url, BarcodeFormat.QR_CODE, size, size, hints) 获得 BitMatrix。
（3）双重循环遍历 BitMatrix 的每个像素位置，将黑色（true）或白色（false）像素写入 Android Bitmap（ARGB_8888 配置）。
（4）通过 bitmap.asImageBitmap() 转换为 Compose 可用的 ImageBitmap，在 Image 组件中显示。
此方案完全离线运行，不产生任何网络开销，且二维码内容可灵活配置。

4.5 图片加载的容错处理

问题描述：菜品和店铺图片来源于 Unsplash 外网 URL，在国内网络环境下可能加载缓慢或完全无法加载。默认的 AsyncImage 在加载失败时仅显示灰色占位区域，用户体验不佳。

解决方案：使用 Coil 的 SubcomposeAsyncImage 组件替代 AsyncImage，分别通过 loading 和 error 参数提供自定义的加载中和加载失败 Composable：
- loading 状态：白色背景 + 灰色图标（Icons.Outlined.Image）+ "加载中..." 文字提示。
- error 状态：白色背景 + 灰色图标 + 菜品名称（保证用户知道当前查看的菜品）+ "图片加载失败" 文字提示。
此方案确保在任何网络条件下页面都不会出现空白或难看的灰色方块，始终有清晰的信息传达。

4.6 Navigation Compose 的返回栈管理

问题描述：支付流程涉及多个页面连续跳转（Checkout → Payment → OrderSuccess → Home）。如果简单地使用 navController.navigate() 串联，用户从支付成功页按返回键会回到支付页，造成逻辑混乱。需要在特定节点清理返回栈。

解决方案：精确定义每个导航步骤的 popUpTo 策略：
- Checkout → Payment：使用 popUpTo(Screen.Home.route)，将确认订单页从栈中移除，确保用户从支付页返回时直接回到首页而非回到已失效的确认订单页。
- Payment → OrderSuccess：使用 popUpTo(Screen.Payment.route) { inclusive = true }，将支付页本身也从栈中移除，支付完成后不可回退到支付页。
- OrderSuccess → Home：使用 popBackStack(Screen.Home.route, inclusive = false)，弹出支付成功页，保留首页。
最终返回栈仅保留 Home，保证了用户导航体验的合理性。


第5章 总结与展望

5.1 项目成果

本项目成功开发了一款功能完整、架构规范的极简外卖点餐 Android 应用 BiteLite。应用实现了从店铺浏览、菜品选择、购物车管理、订单确认、模拟支付到订单历史查看的端到端业务流程，共计 7 个页面、30 余个 Kotlin 源文件、40 余个 Composable 可复用组件。

技术层面，项目严格遵循 MVVM + UDF 架构范式，采用 Jetpack Compose 声明式 UI 构建界面，通过 Hilt 实现依赖注入，利用 Coroutines + StateFlow 管理异步和响应式数据流，集成 Coil 加载图片、ZXing 生成二维码。所有 UI 组件高度模块化，ViewModel 与 UI 完全解耦，Repository 接口为未来接入真实后端预留了清晰的扩展点。

5.2 技术收获

通过本项目的开发实践，主要收获了以下技术经验：
（1）深入理解了 Compose 声明式 UI 的编程模型和重组机制，能够熟练运用 State、StateFlow、collectAsState 等状态管理工具。
（2）掌握了 MVVM + UDF 在实际项目中的落地方案，尤其是跨页面状态共享（CartManager 单例 + Flow）和复杂 UI 状态（三态：加载中/错误/正常）的设计模式。
（3）熟悉了 Hilt 依赖注入在 Compose 项目中的配置方式，包括 @HiltViewModel、@EntryPoint 的使用场景。
（4）实践了 Navigation Compose 的路由设计和返回栈管理策略。
（5）解决了 Kotlin 字符串模板解析、图片加载容错、本地二维码生成等具体技术问题，积累了实际调试经验。

5.3 不足与改进方向

（1）数据持久化：当前订单数据仅存储在内存中（MockAppRepository 的 MutableList），应用重启后历史订单丢失。后续应引入 Room 数据库实现本地持久化存储。
（2）网络层对接：当前使用 Mock 假数据，后续应接入 Retrofit + OkHttp + Gson/Moshi 构建真实的网络数据层，对接后端 RESTful API。
（3）用户系统：当前缺少用户登录/注册功能，地址和订单未与用户账户关联。后续可集成 Firebase Authentication 或自建用户系统。
（4）地址管理：当前仅在确认订单页支持单次地址编辑，缺少完整的地址簿管理（增删改查、设置默认地址）功能。
（5）图片优化：当前直接使用 Unsplash 原始 URL，未经过 CDN 加速或尺寸裁剪。后续可接入图片处理服务（如七牛、阿里云OSS）实现按需缩略图生成。
（6）单元测试：当前缺少单元测试和 UI 测试覆盖。后续应为 ViewModel（费用计算逻辑、购物车操作）和 Repository（数据查询）编写 JUnit + Mockito 单元测试，为关键 UI 流程编写 Compose UI 测试。
（7）无障碍适配：部分 contentDescription 标注不够完善，后续应补充语义标签，确保 TalkBack 等辅助功能的可用性。


附录A：项目文件结构

app/src/main/java/com/example/dashdine/
├── BiteLiteApp.kt                         // Application 入口（@HiltAndroidApp）
├── MainActivity.kt                        // 主 Activity
├── di/
│   └── AppModule.kt                       // Hilt DI 模块（@Binds Repository）
├── data/
│   ├── model/
│   │   ├── Category.kt                    // 分类数据类
│   │   ├── Dish.kt                        // 菜品、规格组、规格选项数据类
│   │   ├── Order.kt                       // 订单、购物车项、订单状态枚举
│   │   └── Store.kt                       // 店铺数据类
│   ├── mock/
│   │   └── MockData.kt                    // 假数据提供者（5 店，32+ 菜品）
│   └── repository/
│       ├── AppRepository.kt               // 数据仓库接口（8 个 suspend 方法）
│       └── MockAppRepository.kt           // Mock 仓库实现（delay 模拟延迟）
├── navigation/
│   ├── NavGraph.kt                        // 导航图（7 个路由 + 返回栈管理）
│   └── Screen.kt                          // 路由定义（密封类 + createRoute）
└── ui/
    ├── theme/
    │   ├── Color.kt                       // 16 种语义化颜色常量
    │   ├── Theme.kt                       // Material 3 主题配置
    │   └── Type.kt                        // 8 级字体样式定义
    ├── components/
    │   ├── DishCard.kt                    // 菜品卡片（图片+信息+Stepper）
    │   ├── SearchBar.kt                   // 圆角搜索栏
    │   ├── Stepper.kt                     // 通用 +/- 数量步进器
    │   └── StoreCard.kt                   // 店铺卡片（Logo+信息+标签）
    ├── home/
    │   ├── HomeScreen.kt                  // 首页（Header+金刚区+店铺列表）
    │   └── HomeViewModel.kt               // 首页 ViewModel
    ├── store/
    │   ├── StoreDetailScreen.kt           // 店铺详情（双联动+悬浮购物车）
    │   └── StoreDetailViewModel.kt        // 店铺详情 ViewModel
    ├── dish/
    │   ├── DishDetailScreen.kt            // 菜品详情（沉浸式大图+规格+加购）
    │   └── DishDetailViewModel.kt         // 菜品详情 ViewModel
    ├── cart/
    │   ├── CartEntryPoint.kt              // Hilt EntryPoint（Dialog 获取 CartManager）
    │   ├── CartManager.kt                 // 全局购物车管理器（Singleton + StateFlow）
    │   └── CartSheet.kt                   // 购物车 BottomSheet（Dialog 实现）
    ├── checkout/
    │   ├── CheckoutScreen.kt              // 确认订单（地址+明细+备注+支付）
    │   ├── CheckoutViewModel.kt           // 确认订单 ViewModel（费用实时重算）
    │   └── OrderSuccessScreen.kt          // 支付成功页（弹性动画+扩散圆）
    ├── payment/
    │   └── PaymentScreen.kt               // 支付页（微信/支付宝切换+ZXing 二维码）
    └── order/
        ├── OrderListScreen.kt             // 订单历史页（状态标签+金额+时间）
        └── OrderListViewModel.kt          // 订单列表 ViewModel


附录B：技术栈版本清单

开发语言：Kotlin 2.1.10
UI 框架：Jetpack Compose BOM 2024.12.01（Material 3）
导航：Navigation Compose 2.8.8
依赖注入：Hilt 2.53.1 + Hilt Navigation Compose 1.2.0
生命周期：Lifecycle Runtime Compose 2.8.7
图片加载：Coil 2.6.0
协程：Kotlinx Coroutines Android 1.9.0
二维码：ZXing Core 3.5.3
Activity：Activity Compose 1.10.0
核心库：AndroidX Core KTX 1.15.0
注解处理：KSP 2.1.10-1.0.29
构建工具：Gradle 8.9.0 + AGP 8.9.0
最低 SDK：Android 7.0（API 24）
目标 SDK：Android 14（API 35）

（注：本报告中所有截图区域留空，请自行插入对应页面运行截图）
