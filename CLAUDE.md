# WargameGDX — CLAUDE.md

<!-- ==================== LAZY READING PROTOCOL ====================
AGENT: Lee SOLO este fichero al iniciar sesión. NO preleas otros ficheros.
Carga ficheros lazily: solo cuando el prompt del usuario lo requiera explícitamente.

Para leer una sección dentro de un fichero:
  1. Grep del §NombreSección → obtener número de línea
  2. Leer ~40 líneas desde ahí (ajustar si la sección es mayor)
  Nunca leer un fichero entero salvo que el usuario pida revisión completa.

REGLA DE ÍNDICE VIVO: Si añades o renombras una §Sección en cualquier fichero de doc,
actualiza la fila correspondiente en la tabla índice de este fichero en la misma sesión.
================================================================ -->

## Contexto del proyecto

Implementación digital de **Mighty Armies: Ancients** (wargame de miniaturas, antigüedad, 15mm).
Dos ejércitos de 60 AP se enfrentan en un tablero de 36"×24". Turno: tirada de Move Points → movimiento → disparo → combate.

**Fase actual — solo geometría y movimiento:**
- Elementos (unidades) como rectángulos con posición, facing, ancho y profundidad
- Movimiento libre de unidades individuales y grupos (base contact)
- Sin combat, sin tipos de unidad, sin terreno, sin army building
- Reglas de referencia: Mighty Armies: Ancients + DBA 2.2 (geometría)
- Unidad de mundo: **inches** (1 BW = 1.6"). Coordenadas libGDX Y-up.

**Stack:** Kotlin · libGDX 1.12.1 · KTX · LWJGL3 (desktop) · Android

## Onboarding de agente

1. Lee este fichero (ya lo estás haciendo).
2. Si la tarea toca arquitectura o decisiones de diseño → lee `docs/decisions.md`.
3. Si la tarea toca scope o tecnología → lee `docs/scope-and-tech.md`.
4. Para saber dónde está cualquier otro conocimiento → lee `docs/index.md`.
5. Para saber qué ficheros fuente existen → consulta §ClassMap de este fichero.

## Estado actual
- Geometría + selección operativas: 4 elementos pintados como rectángulos azules, bordes blancos, chevrones blancos.
- Selección: click selecciona grupo o elemento individual; Ctrl+click añade/quita del grupo validando conectividad.
- Highlight: elementos seleccionados renderizan con outline amarillo.
- Refactorización reciente: `ElementSelectionSystem` ahora observable via callback (patrón observer); input handling centralizado en `touchDown()`.
- Siguiente paso: movimiento de elementos individuales y grupos.

## Optimizacion de uso de tokens y de contexto
- Es prioritario la minimizacion de uso de tokens. Actua lazily.
- Cuando se va a proceder a hacer una nueva task o cambio significativo en codigo, antes de proceder:
  - evalua si el contexto acumulado es optimo para la tarea
  - si lo es, procede.
  - si no lo es, para. Informa a humano de que es mejor una nueva sesion y por que.
- en general, evalua si el contexto acumulado es optimo para una tarea, y avisa si consideras que es mejor crear nuevo agente

## Workflow Claude-Humano
- **Claude escribe el código; el usuario toma todas las decisiones.**
- Antes de implementar algo no trivial, Claude propone el enfoque en 2-3 líneas y espera aprobación.
- El usuario dirige la arquitectura; Claude ejecuta.
- Lenguaje de comunicación: **español**.

## Workflow de mantenimiento de documentos, archivos MD y test unitarios
- Los ficheros MD y tests unitarios deben quedar actualizados al finalizar una session o humano pide commit o flush
- No actualizar ficheros y tests unitarios en el mismo prompt en el que se escribe codigo. Esto es para permitir modificaciones y refactorizaciones antes de actualizar documentacion md
- Una vez el humano ha validado implementación, pide flush o commit, actualizar documentacion md y tests unitarios
- No crear secciones nuevas o ficheros MD si no es necesario. Modificar o anhadir reglas/informacion a secciones ya existentes en la estructura de docus mds.
- Esta permitido crear secciones o ficheros mds nuevos si ayuda a gestionar orden y estructura de los ficheros y optimiza el consumo de tokens
- El objetivo final es mantener estructura limpia y eficiente.

## Convenciones de código y estilo de codigo
- Codigo y comentarios en ingles.
- Kotlin idiomático — usar KTX siempre que exista extensión equivalente.
- Clases no triviales deben tener un kdoc explicando para que sirve la clase. Objetivo: contribuir al mind map del humano y de ai.
- Comentarios breves y concisos de una linea en statements o declaraciones cuando estos ayudan a entender la logica.
- Comentarios breves y concisos de una linea en ifs, loops, etc, si sirve a entender el codigo segun se va leyendo. Objetivo: reducir carga mental del humano y dejar todo muy claro cuando se lee codigo.
- Preferencia a explicitar tipos, para entender mejor codigo.
- En constructores y paso de parametros:
  - Explicitar el nombre del parametro pasado.
  - Salto de linea para cada parametro. Excepcion: cuando queda mas legible en una linea (ej: ramas de un `when` donde cada rama llama a un metodo con argumentos cortos).
- No añadir abstracciones anticipadas; implementar solo lo pedido.
- Refactorizacion continua: si un cambio de codigo implica una refactorizacion para que el codigo sea mantenible y legible, propon la refactorizacion antes de ejecutar. Tiende a refactorizar sobre aplicar parches y chapuzas, pero informa a humano antes de proceder.

## Convenciones Test unitarios
- mockk para mocks y stubbbings
- primero declarar e instanciar dependencias necesarias en constructor de la clase under test. Instanciar como mockks
- despues declarar e instanciar private val underTest = claseUnderTest(dependencias)
- evitar una fixture global compartida para todos los tests. Declarar fixture adhoc para cada uno de los tes
- estructurar tests en GIVEN, PERFORM, VERIFY. Escribir comentarion con GIVEN, PERFORM, VERIFY para cada bloque del test
- si se esta verificando el return de una funcion usar patron:
    - val actual = underTest.methodUnderTest()
    - val expected = (instanciar expected)
    - assertXXXXX(expected, actual)

## Alias
Algunos alias para workflow humano-ai:

- cl pregunta = "Class and Line": responder con la clase y numero de linea donde esta implementado lo que se pregunta, seguido de una explicacion concisa de una linea.
  - ej: `cl reproduccion de musica` → `GameScreen.57 — la musica se ejecuta con el objeto Music`
- flush: actualizar §ClassMap + §Backlog en este fichero + docs/index.md si hay secciones nuevas. 
- commit: flush + commit
- exp: explicacion del codigo escrito en el prompt, o explicacion del razonamiento hecho
- anal X: analisis de X. X puede ser una peticion, una idea, etc. Analiza la correccion de la sugerencia/rezonamiento del humano. Busca fallos, critica constructiva, alternativas estrategicas.
- add X: x puede ser una nueva regla de estilo, una nueva convencion de codigo, una nueva norma de colaboracion en el workflow, una nueva norma de agentes etc. Gestionar la norma como se discribe a continuacion.
- task X: X es una tarea, gestionar la tarea como explicado a continuacion

## Modificación incremental interactiva de workflow y configuración claude
- Si te pido anhadir regla de estilo, una nueva convencion de codigo, una nueva norma de colaboracion en el workflow, una nueva norma de agentes etc:
- evaluacion de si la peticion es correcta y coherente con el resto del workflow. si todo bien, anhadir inmediatamente a fichero md en el lugar apropiado
- la peticion se puede hacer con el alias "add" o formulado en lenguaje natural

## Backlog

| # | Tarea                                               | Notas |
|---|-----------------------------------------------------|-------|
| 1 | Extraer `groupBounds()` a utilidad compartida `GeometryUtils` o extension en `Element` | Necesaria para futuro: renders de group bounding box, collision checks, UI |


## ClassMap

| Clase | Paquete | Propósito | Relaciones clave |
|-------|---------|-----------|------------------|
| `WarGame` | `wargame` | Entry point; arranca la app y lanza `WargameScreen` | Extiende `KtxGame` |
| `WargameScreen` | `wargame.ui` | Pantalla principal; crea `GameEngine`, delega render, maneja input | Usa `GameEngine`, `ElementSelectionSystem` |
| `GameEngine` | `wargame.engine` | Colección de `Actor`s; `addActor`, `removeActor`, `getActorById<T>`, render con autoShapeType | Itera `List<Actor>` |
| `Actor` | `wargame.engine` | Interfaz: `id: String` + `render(ShapeRenderer, delta)` | — |
| `ElementActor` | `wargame.engine` | Renderiza un `Element`: fill azul, borde y chevron blancos, outline amarillo si selected | Implementa `Actor`, lee `Element` |
| `Element` | `wargame.logic` | Datos espaciales de una unidad: posición, ángulo, tamaño; hit-test con polígono rotado | — |
| `GameState` | `wargame.logic` | Contenedor: `List<Element>` que representa el estado del tablero | — |
| `InitialElementsFactory` | `wargame.logic` | Crea el layout inicial: A aislada, B/C/D en formación flanco a flanco | Produce `List<Element>` |
| `ElementSelectionSystem` | `wargame.ui` | Gestiona selección: click selecciona grupo, Ctrl+click añade/quita validando conectividad; observable con callback on change | Usa `GroupDetector`, `GameState`; callback a `WargameScreen` |
| `GroupDetector` | `wargame.logic` | BFS para detección de grupos: `findGroup()`, `isConnectedSubgroup()` | Lee `Element` contacto |

## Workflow relativo a tareas y backlog
Cuando se menciona una nueva tarea, requisito, elemento de backlog, TODO etc:
- el humano quiere hacer esa tarea, pero no necesariamente a continuacion
- analisis y critica de X. sugerencia estrategica de cuando hacer X en el backlog.
- Si no hay discrepancia, actualizar backlog/lista tareas con la tarea a realizar. insertarla en mejor lugar estrategico.
- Si hay discrepancias, informacion insuficiente o contradisctoria, etc, preguntar a humano o sugerer improvement antes de actualizar.
