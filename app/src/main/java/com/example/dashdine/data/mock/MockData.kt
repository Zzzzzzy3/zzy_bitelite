package com.example.dashdine.data.mock

import com.example.dashdine.data.model.*

/**
 * Mock 数据提供者 — 提供逼真的假数据用于开发和测试
 */
object MockData {

    // ── 店铺列表 ─────────────────────────────────────────────

    val stores = listOf(
        Store(
            id = "s1",
            name = "老王家的味道",
            logoUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=200&h=200&fit=crop",
            coverUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&h=400&fit=crop",
            rating = 4.8f,
            monthlySales = 3286,
            minOrderPrice = 20f,
            deliveryFee = 3f,
            deliveryTime = "30-45min",
            tags = listOf("满30减5", "新店特惠", "口碑好店"),
            distance = "1.2km",
            isNew = true,
            hasDiscount = true
        ),
        Store(
            id = "s2",
            name = "川味小馆",
            logoUrl = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=200&h=200&fit=crop",
            coverUrl = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=800&h=400&fit=crop",
            rating = 4.6f,
            monthlySales = 5602,
            minOrderPrice = 15f,
            deliveryFee = 0f,
            deliveryTime = "25-40min",
            tags = listOf("免配送费", "满20减3"),
            distance = "0.8km",
            hasDiscount = true
        ),
        Store(
            id = "s3",
            name = "轻食沙拉实验室",
            logoUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200&h=200&fit=crop",
            coverUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800&h=400&fit=crop",
            rating = 4.9f,
            monthlySales = 2100,
            minOrderPrice = 25f,
            deliveryFee = 4f,
            deliveryTime = "35-50min",
            tags = listOf("健康轻食", "低卡"),
            distance = "2.1km"
        ),
        Store(
            id = "s4",
            name = "深夜烧烤屋",
            logoUrl = "https://images.unsplash.com/photo-1558030006-450675393462?w=200&h=200&fit=crop",
            coverUrl = "https://images.unsplash.com/photo-1558030006-450675393462?w=800&h=400&fit=crop",
            rating = 4.5f,
            monthlySales = 8900,
            minOrderPrice = 30f,
            deliveryFee = 5f,
            deliveryTime = "40-60min",
            tags = listOf("夜间营业", "人气爆棚"),
            distance = "3.5km"
        ),
        Store(
            id = "s5",
            name = "和风日料",
            logoUrl = "https://images.unsplash.com/photo-1579027989536-b7b1f875659b?w=200&h=200&fit=crop",
            coverUrl = "https://images.unsplash.com/photo-1579027989536-b7b1f875659b?w=800&h=400&fit=crop",
            rating = 4.7f,
            monthlySales = 4100,
            minOrderPrice = 35f,
            deliveryFee = 3f,
            deliveryTime = "30-45min",
            tags = listOf("品质日料", "刺身新鲜"),
            distance = "1.8km",
            hasDiscount = true
        )
    )

    // ── 分类 ─────────────────────────────────────────────────

    fun categoriesForStore(storeId: String): List<Category> = listOf(
        Category("c1_$storeId", "热销", storeId, dishCount = 5),
        Category("c2_$storeId", "折扣", storeId, dishCount = 3),
        Category("c3_$storeId", "主食", storeId, dishCount = 6),
        Category("c4_$storeId", "小食", storeId, dishCount = 4),
        Category("c5_$storeId", "饮品", storeId, dishCount = 4),
        Category("c6_$storeId", "甜品", storeId, dishCount = 3)
    )

    // ── 菜品 ─────────────────────────────────────────────────

    fun dishesForStore(storeId: String): Map<String, List<Dish>> {
        val dishes = when (storeId) {
            "s1" -> dishesStore1(storeId)
            "s2" -> dishesStore2(storeId)
            else -> dishesStore1(storeId) // 默认菜品
        }
        return dishes.groupBy { it.categoryId }
    }

    private fun dishesStore1(storeId: String): List<Dish> {
        val prefix = storeId
        return listOf(
            // 热销
            Dish(
                id = "${prefix}_d1", name = "招牌红烧肉饭", categoryId = "c1_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?w=400&h=300&fit=crop",
                description = "精选五花肉慢炖2小时，肥而不腻，入口即化。配以秘制酱汁和溏心蛋。",
                price = 32f, originalPrice = 38f, monthlySales = 1280, rating = 4.9f,
                calories = "650kcal", allergens = "含大豆、鸡蛋", ingredients = "五花肉、鸡蛋、米饭、酱油、冰糖"
            ),
            Dish(
                id = "${prefix}_d2", name = "香辣鸡腿堡套餐", categoryId = "c1_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&h=300&fit=crop",
                description = "整块鸡腿肉腌制后炸至金黄，搭配新鲜蔬菜和芝士，配薯条和可乐。",
                price = 28f, originalPrice = 35f, monthlySales = 980, rating = 4.7f,
                calories = "780kcal", ingredients = "鸡腿肉、面包、芝士、生菜、薯条"
            ),
            Dish(
                id = "${prefix}_d3", name = "番茄牛肉面", categoryId = "c1_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1598866594230-a7c12756260f?w=400&h=300&fit=crop",
                description = "新鲜番茄熬制汤底，大块牛腩配手工拉面，暖心暖胃。",
                price = 26f, originalPrice = 30f, monthlySales = 860, rating = 4.8f,
                calories = "520kcal", allergens = "含小麦",
                specs = listOf(
                    SpecGroup("辣度", listOf(SpecOption("sp1", "不辣"), SpecOption("sp2", "微辣"), SpecOption("sp3", "中辣")))
                )
            ),
            Dish(
                id = "${prefix}_d4", name = "糖醋里脊盖饭", categoryId = "c1_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1525755662778-989d0524087e?w=400&h=300&fit=crop",
                description = "外酥里嫩的猪里脊，裹上酸甜可口的糖醋汁，配白米饭。",
                price = 24f, originalPrice = 28f, monthlySales = 720, rating = 4.6f,
                calories = "590kcal", ingredients = "猪里脊、番茄酱、糖、醋、米饭"
            ),
            Dish(
                id = "${prefix}_d5", name = "经典麻婆豆腐", categoryId = "c1_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1582452919408-aca2d1e5f78a?w=400&h=300&fit=crop",
                description = "正宗川味麻婆豆腐，麻辣鲜香嫩，下饭神器。选用嫩豆腐搭配牛肉末。",
                price = 18f, originalPrice = 22f, monthlySales = 650, rating = 4.8f,
                calories = "380kcal", allergens = "含大豆",
                specs = listOf(
                    SpecGroup("辣度", listOf(SpecOption("sp1", "微辣"), SpecOption("sp2", "中辣"), SpecOption("sp3", "特辣")))
                )
            ),
            // 折扣
            Dish(
                id = "${prefix}_d6", name = "宫保鸡丁套餐", categoryId = "c2_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1529563021893-cc83c992d75d?w=400&h=300&fit=crop",
                description = "鸡丁嫩滑，花生酥脆，经典宫保口味，配米饭和小菜。限时特价！",
                price = 19.9f, originalPrice = 32f, monthlySales = 1560, rating = 4.7f,
                calories = "550kcal", allergens = "含花生、大豆", ingredients = "鸡胸肉、花生、干辣椒、黄瓜"
            ),
            Dish(
                id = "${prefix}_d7", name = "日式咖喱猪排饭", categoryId = "c2_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1593508512255-86ab3a157998?w=400&h=300&fit=crop",
                description = "厚切猪排炸至金黄酥脆，搭配浓郁日式咖喱，满足感爆棚。",
                price = 29.9f, originalPrice = 42f, monthlySales = 890, rating = 4.8f,
                calories = "720kcal", allergens = "含小麦", ingredients = "猪排、咖喱、米饭、胡萝卜、土豆"
            ),
            Dish(
                id = "${prefix}_d8", name = "酸菜鱼单人份", categoryId = "c2_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1583032015879-e5022cb87c3b?w=400&h=300&fit=crop",
                description = "新鲜鱼片配老坛酸菜，酸爽开胃，单人份刚刚好。",
                price = 35.9f, originalPrice = 48f, monthlySales = 670, rating = 4.6f,
                calories = "410kcal", ingredients = "鱼片、酸菜、辣椒、花椒"
            ),
            // 主食
            Dish(
                id = "${prefix}_d9", name = "蛋炒饭", categoryId = "c3_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400&h=300&fit=crop",
                description = "粒粒分明的黄金蛋炒饭，简单却美味的经典。",
                price = 12f, monthlySales = 2300, rating = 4.5f,
                calories = "480kcal", allergens = "含鸡蛋", ingredients = "米饭、鸡蛋、葱花"
            ),
            Dish(
                id = "${prefix}_d10", name = "红烧牛肉面", categoryId = "c3_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1555126634-323283e090fa?w=400&h=300&fit=crop",
                description = "大块牛腱肉慢炖4小时的浓郁汤头，配手工拉面。",
                price = 28f, originalPrice = 32f, monthlySales = 1100, rating = 4.8f,
                calories = "580kcal", allergens = "含小麦", ingredients = "牛腱肉、面条、萝卜、香料"
            ),
            Dish(
                id = "${prefix}_d11", name = "腊味煲仔饭", categoryId = "c3_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=400&h=300&fit=crop",
                description = "广式腊肠和腊肉铺在米饭上煲制，底部有金黄锅巴。",
                price = 30f, monthlySales = 780, rating = 4.7f,
                calories = "620kcal", ingredients = "米饭、腊肠、腊肉、酱油"
            ),
            Dish(
                id = "${prefix}_d12", name = "扬州炒饭", categoryId = "c3_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400&h=300&fit=crop",
                description = "虾仁、叉烧、青豆、鸡蛋，料足味美的经典炒饭。",
                price = 22f, monthlySales = 920, rating = 4.6f,
                calories = "550kcal", allergens = "含虾、鸡蛋", ingredients = "米饭、虾仁、叉烧、青豆、鸡蛋"
            ),
            Dish(
                id = "${prefix}_d13", name = "肉末茄子盖饭", categoryId = "c3_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1574484284002-952d92456975?w=400&h=300&fit=crop",
                description = "茄子软烂入味，肉末酱香浓郁，下饭一绝。",
                price = 20f, monthlySales = 850, rating = 4.7f,
                calories = "470kcal", ingredients = "茄子、猪肉末、蒜、酱油、米饭"
            ),
            Dish(
                id = "${prefix}_d14", name = "海鲜炒面", categoryId = "c3_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&h=300&fit=crop",
                description = "鲜虾、鱿鱼搭配蔬菜炒制，锅气十足！",
                price = 25f, monthlySales = 630, rating = 4.5f,
                calories = "520kcal", allergens = "含虾、小麦、贝类", ingredients = "面条、虾仁、鱿鱼、蔬菜"
            ),
            // 小食
            Dish(
                id = "${prefix}_d15", name = "香酥炸鸡翅 (4只)", categoryId = "c4_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=400&h=300&fit=crop",
                description = "外酥里嫩的炸鸡翅，配以秘制香料，香脆可口。",
                price = 16f, monthlySales = 1800, rating = 4.8f,
                calories = "320kcal", ingredients = "鸡翅、面粉、香料"
            ),
            Dish(
                id = "${prefix}_d16", name = "凉拌黄瓜", categoryId = "c4_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1606277852392-8da1fc3f6caa?w=400&h=300&fit=crop",
                description = "清脆爽口，蒜香四溢，解腻必备。",
                price = 8f, monthlySales = 1200, rating = 4.5f,
                calories = "80kcal", ingredients = "黄瓜、蒜、醋、辣椒油"
            ),
            Dish(
                id = "${prefix}_d17", name = "春卷 (6个)", categoryId = "c4_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1606525437817-0035a5f0a0e1?w=400&h=300&fit=crop",
                description = "金黄酥脆的春卷，馅料丰富，蘸甜辣酱更美味。",
                price = 14f, monthlySales = 950, rating = 4.6f,
                calories = "280kcal", allergens = "含小麦", ingredients = "春卷皮、蔬菜、粉丝"
            ),
            Dish(
                id = "${prefix}_d18", name = "蒜蓉西兰花", categoryId = "c4_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1584278941520-2aeb1bc5e4b7?w=400&h=300&fit=crop",
                description = "清爽健康的蒜蓉炒西兰花，清淡不油腻。",
                price = 12f, monthlySales = 680, rating = 4.4f,
                calories = "120kcal", ingredients = "西兰花、蒜、橄榄油"
            ),
            // 饮品
            Dish(
                id = "${prefix}_d19", name = "冰镇柠檬水", categoryId = "c5_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1621263764928-df1444c5e859?w=400&h=300&fit=crop",
                description = "新鲜柠檬榨汁，冰凉清爽，解暑必备。",
                price = 8f, monthlySales = 2500, rating = 4.7f,
                calories = "60kcal"
            ),
            Dish(
                id = "${prefix}_d20", name = "珍珠奶茶", categoryId = "c5_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1558857563-b371033873b8?w=400&h=300&fit=crop",
                description = "经典台湾珍珠奶茶，Q弹珍珠配浓郁奶茶。",
                price = 15f, monthlySales = 1900, rating = 4.8f,
                calories = "350kcal", allergens = "含乳制品",
                specs = listOf(
                    SpecGroup("甜度", listOf(SpecOption("sw1", "无糖"), SpecOption("sw2", "半糖"), SpecOption("sw3", "全糖"))),
                    SpecGroup("温度", listOf(SpecOption("tp1", "常温"), SpecOption("tp2", "去冰"), SpecOption("tp3", "加冰")))
                )
            ),
            Dish(
                id = "${prefix}_d21", name = "冰美式咖啡", categoryId = "c5_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400&h=300&fit=crop",
                description = "精选阿拉比卡豆，冷水慢萃，纯粹咖啡体验。",
                price = 18f, monthlySales = 1400, rating = 4.6f,
                calories = "15kcal"
            ),
            Dish(
                id = "${prefix}_d22", name = "鲜榨橙汁", categoryId = "c5_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=400&h=300&fit=crop",
                description = "100%鲜榨橙汁，不加糖不加水，满满维C。",
                price = 16f, monthlySales = 1100, rating = 4.7f,
                calories = "120kcal"
            ),
            // 甜品
            Dish(
                id = "${prefix}_d23", name = "抹茶提拉米苏", categoryId = "c6_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&h=300&fit=crop",
                description = "日式抹茶与意式提拉米苏的完美融合，微苦回甘。",
                price = 22f, monthlySales = 720, rating = 4.9f,
                calories = "280kcal", allergens = "含乳制品、鸡蛋", ingredients = "抹茶粉、马斯卡彭芝士、手指饼干"
            ),
            Dish(
                id = "${prefix}_d24", name = "芒果糯米饭", categoryId = "c6_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1621293954908-907159247fc8?w=400&h=300&fit=crop",
                description = "泰国芒果配椰浆糯米，香甜软糯，热带风情。",
                price = 18f, monthlySales = 850, rating = 4.7f,
                calories = "340kcal", allergens = "含乳制品", ingredients = "芒果、糯米、椰浆、糖"
            ),
            Dish(
                id = "${prefix}_d25", name = "巧克力熔岩蛋糕", categoryId = "c6_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=400&h=300&fit=crop",
                description = "切开后巧克力岩浆缓缓流出，搭配香草冰淇淋，冰火两重天。",
                price = 26f, monthlySales = 580, rating = 4.8f,
                calories = "420kcal", allergens = "含乳制品、鸡蛋、小麦", ingredients = "巧克力、黄油、鸡蛋、面粉"
            )
        )
    }

    private fun dishesStore2(storeId: String): List<Dish> {
        val prefix = storeId
        return listOf(
            // 热销
            Dish(
                id = "${prefix}_d1", name = "水煮鱼", categoryId = "c1_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1583032015879-e5022cb87c3b?w=400&h=300&fit=crop",
                description = "麻辣鲜香的经典水煮鱼，鱼片嫩滑，配豆芽和莴笋。",
                price = 48f, monthlySales = 2100, rating = 4.9f,
                calories = "520kcal", ingredients = "鱼片、豆芽、莴笋、花椒、干辣椒",
                specs = listOf(
                    SpecGroup("辣度", listOf(SpecOption("sp1", "微辣"), SpecOption("sp2", "中辣"), SpecOption("sp3", "特辣")))
                )
            ),
            Dish(
                id = "${prefix}_d2", name = "回锅肉", categoryId = "c1_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1529563021893-cc83c992d75d?w=400&h=300&fit=crop",
                description = "川菜之魂！五花肉煸至焦香，配蒜苗和豆瓣酱。",
                price = 32f, originalPrice = 38f, monthlySales = 1500, rating = 4.8f,
                calories = "580kcal", ingredients = "五花肉、蒜苗、豆瓣酱"
            ),
            Dish(
                id = "${prefix}_d3", name = "担担面", categoryId = "c1_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1555126634-323283e090fa?w=400&h=300&fit=crop",
                description = "芝麻酱和肉末的完美结合，麻辣鲜香，一碗不过瘾。",
                price = 16f, monthlySales = 1800, rating = 4.7f,
                calories = "450kcal", allergens = "含花生、小麦", ingredients = "面条、芝麻酱、肉末、花生碎",
                specs = listOf(
                    SpecGroup("辣度", listOf(SpecOption("sp1", "不辣"), SpecOption("sp2", "微辣"), SpecOption("sp3", "正常辣")))
                )
            ),
            // 折扣
            Dish(
                id = "${prefix}_d4", name = "麻辣香锅 (小份)", categoryId = "c2_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1623689046285-0b5361750b60?w=400&h=300&fit=crop",
                description = "自选食材，麻辣炒制，一人食小份刚刚好。限时折扣！",
                price = 28.9f, originalPrice = 42f, monthlySales = 1200, rating = 4.7f,
                calories = "650kcal", ingredients = "各种蔬菜、肉类、火锅底料"
            ),
            // 主食
            Dish(
                id = "${prefix}_d5", name = "辣子鸡丁饭", categoryId = "c3_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400&h=300&fit=crop",
                description = "重庆辣子鸡，干香麻辣，配白米饭，越吃越过瘾。",
                price = 24f, monthlySales = 950, rating = 4.6f,
                calories = "560kcal", ingredients = "鸡腿肉、干辣椒、花椒、米饭"
            ),
            // 小食
            Dish(
                id = "${prefix}_d6", name = "红油抄手 (10个)", categoryId = "c4_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=400&h=300&fit=crop",
                description = "皮薄馅大的抄手，淋上红油和花椒面，一口一个。",
                price = 18f, monthlySales = 1400, rating = 4.8f,
                calories = "380kcal", allergens = "含小麦", ingredients = "猪肉、面皮、红油、花椒"
            ),
            // 饮品
            Dish(
                id = "${prefix}_d7", name = "老成都冰粉", categoryId = "c5_$storeId", storeId = storeId,
                imageUrl = "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=400&h=300&fit=crop",
                description = "手工冰粉配红糖水和各种配料，冰凉解辣。",
                price = 10f, monthlySales = 2000, rating = 4.9f,
                calories = "180kcal", ingredients = "冰粉、红糖、花生碎、葡萄干"
            )
        )
    }

    // ── 默认地址 ─────────────────────────────────────────────

    data class AddressInfo(
        val name: String = "用户",
        val phone: String = "130****0000",
        val address: String = "北京市朝阳区某某街 100 号"
    )

    val defaultAddress = AddressInfo()
}
