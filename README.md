<div align="center">

# \# --- [ CIM | Custom Item Model ] --- \#
# Give your items a little life.

---

## - [ Description | Описание ] -

[EN] CIM (Custom Item Model) adds support for fully-custom animated item models
using simple vanilla-style JSON.

[RU] CIM добавляет поддержку полноценных кастомных и АНИМИРУЕМЫХ предметных
моделей через обычный JSON

---

## - [ How it works | Как работает ] -

<div align="left">

> [EN]
> Step 1: Create in your resourcepack next folders/directories:  
> - `geo`, `animations`,  
> - `models/item`, `textures/item`.
> ---
> 
> [RU]
> Шаг 1: Создайте в своём ресурспаке следующие папки/директории:
> - `geo`, `animations`,  
> - `models/item`, `textures/item`.

> [EN]
> Step 2: Place models/animations/textures/display:  
> - Models → `geo/example.geo.json`,
> - Animations → `animations/example.animation.json`,
> - Textures → `textures/item/example.png` (and `example.png.mcmeta` for animated textures),
> - Displays model → `models/item/example.json`.
> 
> `example` is your unique model name.
> 
> ---
> 
> [RU]
> Шаг 2: Расположите свои модели/анимации/текстуры/отображение:
> - Модели → `geo/example.geo.json`,
> - Анимации → `animations/example.animation.json`,
> - Текстуры → `textures/item/example.png` (and `example.png.mcmeta` for animated textures),
> - Отображения → `models/item/example.json`.
> 
> `example` - это уникальное имя модели.

> Step 3:  
> Edit your display model and add:
> ```json
> {
>     "render_provider": "cim"
> }
> ```

> Step 4:  
> Add `custom_model_data` override in an item:
> ```json
> {
>   "overrides": [
>     {
>       "predicate": { "custom_model_data": 1 },
>       "model": "cim:item/example"
>     }
>   ]
> }
> ```
> where:
> `1` - unique id for this model  
> `cim:item/example` - path to your display model

> Step 5: Enjoy!

</div>

---

## - [ Planned ] -

> - Support for multiple animation states (drop, hold, gui..)

</div>