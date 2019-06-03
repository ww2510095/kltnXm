CREATE OR REPLACE TRIGGER addSaleSize
  BEFORE INSERT ON Orderrelevance
  FOR EACH ROW

BEGIN
  update Commoditykey
     set SaleSize = SaleSize + :new.num
   where id = (select commoditykeyid from commodity where id = :new.itemid);
END;
