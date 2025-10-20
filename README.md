1. userInSession - не нужен он в контроллере на удаление пользователя - Corrected
2. UserController - не должно быть адмниских функций - Corrected
3. public void updateUser - сократить - Corrected
4. 
5. нет секьюрности на user/** - Corrected
6. .antMatchers("/").permitAll() - сначала самые сильные проверки должны быть, потом самые слабые - Corrected
7. @Autowired - не надо ставить над конструктором спринг и так сам поймет - Corrected
8. @GetMapping("") - -кавычки зачем? - Corrected
9. Optional -не используй в контроллерах - Corrected
10. public String saveUser - разделить на 2 контроллера, убрать бизнес логику в сервисный слой - Corrected
11. User userInSession = userService.getUserByName(principal.getName()).orElseThrow(); - это  лишняя конструкция - Corrected
12. @Autowired UserService userService; - через конструктор + заприватить - Corrected
13. public String userInfo( - упростить, остальное убрать - Corrected
14. public void updateUser - сократить__ - Corrected