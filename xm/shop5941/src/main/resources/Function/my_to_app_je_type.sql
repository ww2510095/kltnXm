create or replace function my_to_app_je_type(type     number,
                                             memberid number,
                                             member1  number,
                                             member2  number,
                                             member3  number,
                                             member4  number) return number is

  flag number := 0;
begin
  if (type = 1) then
    if (memberid = member1) then
      flag := 1;
    
    end if;
  elsif (type = 2) then
    if (memberid = member2) then
      flag := 2;
    
    end if;
  elsif (type = 3) then
    if (memberid = member3) then
      flag := 3;
    
    end if;
  elsif (type = 4) then
    if (memberid = member4) then
      flag := 4;
    
    end if;
  
  end if;

  if (type = flag) then
    return 1;
  else
    return 0;
  end if;

end;
