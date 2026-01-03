INSERT INTO categorias (nome, icone, ativo) VALUES
('Eletricista', 'flash', true),
('Encanador', 'water', true),
('Pintor', 'color-palette', true),
('Pedreiro', 'construct', true),
('Marceneiro', 'hammer', true),
('Jardineiro', 'leaf', true),
('Limpeza', 'sparkles', true),
('Ar Condicionado', 'snow', true),
('Eletrodomésticos', 'settings', true),
('Chaveiro', 'key', true),
('Pisos e Revestimentos', 'layers', true),
('Serralheiro', 'cut', true);

SELECT id, nome, icone, ativo FROM categorias ORDER BY nome;
