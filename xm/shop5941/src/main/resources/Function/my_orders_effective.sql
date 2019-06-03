create or replace function my_orders_effective(memberid number,
                                               member1  number,
                                               member2  number,
                                               member3  number,
                                               member4  number)
  return number is

  flag number := 0;
begin

  if (memberid = member1) then
    flag := 1;
  elsif (memberid = member2) then
    flag := 2;
  elsif (memberid = member3) then
    flag := 3;
  elsif (memberid = member4) then
    flag := 4;
  else
    flag := 0;
  end if;
  return flag;

end;
