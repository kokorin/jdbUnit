SUser
===============================================
| id:Integer | login:String | password:String |
|:----------:|:------------:|:---------------:|
|     1      |   admin      |      admin      |
|    :X:     |    test      |       test      |

SRole
============================
| id:Integer | name:String |
|:----------:|:-----------:|
|     1      | ROLE_ADMIN  |
|    :Z:     | ROLE_TEST   |

SUser_SRole
=====================================
| user_id:Integer | role_id:Integer |
|:---------------:|:---------------:|
|        1        |       1         |
|       =X=       |      =Z=        |