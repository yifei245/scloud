CREATE DATABASE IF NOT EXISTS scloud DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE scloud;

CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  nickname VARCHAR(64),
  mobile VARCHAR(32),
  email VARCHAR(128),
  status TINYINT NOT NULL DEFAULT 1,
  dept_id BIGINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_name VARCHAR(64) NOT NULL,
  role_key VARCHAR(64) NOT NULL UNIQUE,
  data_scope TINYINT NOT NULL DEFAULT 1,
  status TINYINT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色';

CREATE TABLE sys_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT NOT NULL DEFAULT 0,
  menu_name VARCHAR(64) NOT NULL,
  permission VARCHAR(128),
  type TINYINT NOT NULL DEFAULT 1,
  status TINYINT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单与按钮权限';

CREATE TABLE sys_dept (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT NOT NULL DEFAULT 0,
  dept_name VARCHAR(64) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门';

CREATE TABLE sys_dict (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dict_type VARCHAR(64) NOT NULL,
  dict_label VARCHAR(64) NOT NULL,
  dict_value VARCHAR(64) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典';

CREATE TABLE sys_post (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_code VARCHAR(64) NOT NULL,
  post_name VARCHAR(64) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位';

CREATE TABLE sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色';

CREATE TABLE sys_role_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单权限';

CREATE TABLE demo_product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_name VARCHAR(128) NOT NULL,
  product_code VARCHAR(64) NOT NULL UNIQUE,
  price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  stock INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品';

CREATE TABLE demo_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

INSERT INTO sys_dept(id, parent_id, dept_name, status) VALUES (1, 0, '总部', 1);
INSERT INTO sys_role(id, role_name, role_key, data_scope, status) VALUES (1, '超级管理员', 'admin', 1, 1), (2, '普通用户', 'user', 2, 1);
INSERT INTO sys_menu(parent_id, menu_name, permission, type, status) VALUES
(0, '系统管理', 'system', 1, 1),
(1, '用户查询', 'system:user:query', 2, 1),
(1, '用户新增', 'system:user:create', 2, 1),
(1, '用户修改', 'system:user:update', 2, 1),
(1, '用户删除', 'system:user:delete', 2, 1),
(1, '用户分配角色', 'system:user:assign-role', 2, 1),
(1, '角色查询', 'system:role:query', 2, 1),
(1, '角色新增', 'system:role:create', 2, 1),
(1, '角色修改', 'system:role:update', 2, 1),
(1, '角色删除', 'system:role:delete', 2, 1),
(1, '角色分配菜单', 'system:role:assign-menu', 2, 1),
(1, '菜单查询', 'system:menu:query', 2, 1),
(1, '菜单新增', 'system:menu:create', 2, 1),
(1, '菜单修改', 'system:menu:update', 2, 1),
(1, '菜单删除', 'system:menu:delete', 2, 1),
(1, '部门查询', 'system:dept:query', 2, 1),
(1, '部门新增', 'system:dept:create', 2, 1),
(1, '部门修改', 'system:dept:update', 2, 1),
(1, '部门删除', 'system:dept:delete', 2, 1),
(1, '字典查询', 'system:dict:query', 2, 1),
(1, '字典新增', 'system:dict:create', 2, 1),
(1, '字典修改', 'system:dict:update', 2, 1),
(1, '字典删除', 'system:dict:delete', 2, 1),
(1, '岗位查询', 'system:post:query', 2, 1),
(1, '岗位新增', 'system:post:create', 2, 1),
(1, '岗位修改', 'system:post:update', 2, 1),
(1, '岗位删除', 'system:post:delete', 2, 1),
(0, '商品管理', 'demo:product', 1, 1),
(28, '商品查询', 'demo:product:query', 2, 1),
(28, '商品新增', 'demo:product:create', 2, 1),
(28, '商品修改', 'demo:product:update', 2, 1),
(28, '商品删除', 'demo:product:delete', 2, 1),
(28, '商品购买', 'demo:product:purchase', 2, 1),
(0, '代码生成', 'generator', 1, 1),
(34, '代码生成查询', 'generator:query', 2, 1),
(34, '代码生成执行', 'generator:code', 2, 1);
INSERT INTO sys_dict(dict_type, dict_label, dict_value, status) VALUES ('common_status', '启用', '1', 1), ('common_status', '禁用', '0', 1);
INSERT INTO sys_post(post_code, post_name, status) VALUES ('dev', '开发工程师', 1);
INSERT INTO sys_user(id, username, password, nickname, mobile, email, status, dept_id) VALUES
(1, 'admin', SHA2('admin123', 256), '管理员', '18800000000', 'admin@scloud.local', 1, 1);
INSERT INTO sys_user_role(user_id, role_id) VALUES (1, 1);
INSERT INTO sys_role_menu(role_id, menu_id) SELECT 1, id FROM sys_menu;
INSERT INTO demo_product(product_name, product_code, price, stock, status) VALUES
('演示商品 A', 'SKU-A', 99.00, 100, 1),
('演示商品 B', 'SKU-B', 199.00, 50, 0);
