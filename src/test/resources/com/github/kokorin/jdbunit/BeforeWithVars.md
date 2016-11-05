SUser
===============================================
| id:Integer | login:String | password:String |
|:----------:|:------------:|:---------------:|
|     1      |   admin      |      admin      |

SRole
============================
| id:Integer | name:String |
|:----------:|:-----------:|
|     1      | ROLE_ADMIN  |

SUser_SRole
=====================================
| user_id:Integer | role_id:Integer |
|:---------------:|:---------------:|
|        1        |       1         |