1. 

1. Не понимаю, почему в некоторых контроллерах используешь try catch, в некоторых нет. Сделай в одном стиле - Corrected
2. public String userInfo - название метода привести в порядок - Corrected
3. UserController - не должно быть адмниских функций (сохранить пользователя и обновить его это админские функции) - Corrected

1. userInSession - не нужен он в контроллере на удаление пользователя - Corrected
2. UserController - не должно быть адмниских функций - Corrected
3. public void updateUser - сократить - Corrected

1. нет секьюрности на user/** - Corrected
2. .antMatchers("/").permitAll() - сначала самые сильные проверки должны быть, потом самые слабые - Corrected
3. @Autowired - не надо ставить над конструктором спринг и так сам поймет - Corrected
4. @GetMapping("") - -кавычки зачем? - Corrected
5. Optional -не используй в контроллерах - Corrected
6. public String saveUser - разделить на 2 контроллера, убрать бизнес логику в сервисный слой - Corrected
7. User userInSession = userService.getUserByName(principal.getName()).orElseThrow(); - это  лишняя конструкция - Corrected
8. @Autowired UserService userService; - через конструктор + заприватить - Corrected
9. public String userInfo( - упростить, остальное убрать - Corrected
10. public void updateUser - сократить__ - Corrected
