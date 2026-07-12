# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**BiteLite** — 极简点餐 App：一款主打"小而美"、交互流畅、UI 极简且现代的外卖点餐应用。项目目前处于初始化阶段，尚未开始编码。

完整需求文档见 `开发手册.txt`。

## Tech Stack (Planned)

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3, declarative UI)
- **Architecture**: MVVM + UDF (单向数据流)
- **Async**: Kotlin Coroutines + Flow
- **DI**: Hilt
- **Image Loading**: Coil

## Architecture

采用 Repository 模式，数据层通过 Repository 接口抽象，ViewModel 只负责业务逻辑与状态暴露，不持有任何 UI 引用。

```
UI (Composable) → ViewModel (State/Event) → Repository → Mock Data / Remote API
```

### Code Style Rules (from spec)

- 严格抽离 UI 组件为独立 Composable 函数，避免单文件代码过长。
- ViewModel 只负责业务逻辑与状态暴露，不包含任何 UI 引用。
- 代码要求高内聚低耦合。

## Planned Module / Feature Breakdown

按以下顺序分模块实现（对应开发手册 15.3–15.7）：

1. **15.3 数据层 (Mock Data)** — 定义数据实体（Store, Category, Dish, Order），构建 Repository 接口与假数据实现，模拟网络延迟。
2. **15.4 首页/店铺列表** — 顶部定位+搜索栏，横向金刚区，店铺卡片垂直列表。
3. **15.5 店铺详情** — 折叠吸顶头部，左侧分类导航 + 右侧菜品列表双联动，底部悬浮购物车条。
4. **15.6 菜品详情** — 沉浸式大图，信息面板，可选规格选择，底部加入购物车。
5. **15.7 购物车与结算** — 购物车 BottomSheet，确认订单页（地址、明细、备注、支付），支付成功弹窗。

## UI Design System

- **视觉风格**: Minimalist & Modern，大面积留白（`#F8F9FA`）。
- **主色调**: 珊瑚橘 `#FF7F50`（备选：薄荷绿 `#20B2AA`），仅用于核心 CTA 按钮。
- **排版**: 无衬线字体，辅助文字 `#8A8A8A`。
- **圆角**: 卡片 16–24dp，配合柔和扩散阴影营造悬浮感。
- **动画**: 页面切换平滑过渡，加购抛物线/缩放弹动反馈，列表阻尼回弹。

## Getting Started (Project Not Yet Initialized)

当前项目尚未创建 Android 工程。第一步应使用 Android Studio 创建一个 Kotlin + Jetpack Compose 项目（Minimum SDK 24+，建议 target SDK 34+），按顺序参考上述模块拆解实现。
