-- Dati di prova per il catalogo (da eseguire DOPO schema.sql).
-- Le immagini sono in web/img/prodotti/ (qui si salva solo il nome del file).

INSERT INTO prodotti (nome, descrizione, prezzo, iva, giacenza, immagine) VALUES
('Dentifricio Protezione Totale 75 ml',
 'Dentifricio al fluoro per una protezione quotidiana contro placca e carie, con gusto menta fresca.',
 2.49, 22.00, 50, 'dentifricio.jpg'),

('Penne Rigate 500 g',
 'Pasta di semola di grano duro trafilata al bronzo. Cottura 11 minuti, ottima con ogni sugo.',
 1.29, 4.00, 100, 'pasta.jpg'),

('Detersivo Liquido Lavatrice 1,5 L',
 'Detersivo liquido concentrato per bucato bianco e colorato, circa 25 lavaggi. Profumo fresco.',
 6.90, 22.00, 30, 'detersivo.jpg');
