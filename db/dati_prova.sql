-- Dati di prova: utente amministratore + catalogo (da eseguire DOPO schema.sql).
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
 6.90, 22.00, 30, 'detersivo.jpg'),

('Pane Fresco Artigianale 500 g',
 'Pagnotta e filone di pane fresco a lievitazione naturale, croccante fuori e morbido dentro. Prodotto ogni giorno.',
 2.20, 4.00, 40, 'pane.jpg'),

('Mele Rosse 1 kg',
 'Mele rosse croccanti e succose, ideali da mangiare al naturale o per le torte. Confezione da circa 1 kg.',
 2.49, 4.00, 80, 'mela.jpg'),

('Shampoo Idratante 250 ml',
 'Shampoo delicato per uso quotidiano con formula idratante, per capelli morbidi e luminosi.',
 3.49, 22.00, 60, 'shampoo.jpg'),

('Muesli Frutta e Noci 375 g',
 'Miscela di fiocchi di cereali, frutta secca, mirtilli e lamponi. Ottimo con latte o yogurt a colazione.',
 3.29, 10.00, 50, 'muesli.jpg'),

('Biscotti al Cioccolato 350 g',
 'Frollini croccanti con gocce di cioccolato fondente, perfetti per la colazione o la merenda.',
 2.29, 10.00, 70, 'biscotti.jpg');

-- Utente amministratore di prova: admin@shop.it / password Admin2024!
-- (la colonna password contiene l'hash SHA-256 di "Admin2024!", non la password)
INSERT INTO utenti (nome, cognome, email, password, ruolo) VALUES
('Admin', 'Negozio', 'admin@shop.it',
 '5a55c7873ed7338f35d782adb513d336a36086ddec0fa4b6444fda6d440387c2', 'admin');
