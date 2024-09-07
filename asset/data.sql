-- Se crea el usuario Mario
INSERT INTO user_mva (first_name, last_name, email, nickname, password_hash, activated, language_key)
VALUES ('Mario', 'Martínez', 'mmartinezlanuza@gmail.com', 'MarioML', '$2a$10$YF9Ua8dvEY0I0ufsqGqyEOwTvm371OjyOUKofD/UCJ37G2BnTUPoa', 1, 'es');

INSERT INTO authority VALUES
  ('ROLE_USER'),
  ('ROLE_ADMIN'),
  ('ROLE_UNAUTHORIZE');


