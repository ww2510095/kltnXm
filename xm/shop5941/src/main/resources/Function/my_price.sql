create or replace function my_price(type1 number,
                                    type2 number,
                                    type3 number,
                                    numd  number) return number is

begin
  if (my_null(type1) = 1) then
    return type1;
  elsif (my_null(type2) = 1) then
    return type2;
  elsif (my_null(type3) = 1) then
    return type3;
  else
    return numd;
  end if;

end;
