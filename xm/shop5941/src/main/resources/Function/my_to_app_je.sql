create or replace function my_to_app_je(type     number,
                                        memberid number,
                                        member1  number,
                                        member2  number,
                                        member3  number,
                                        member4  number,
                                        num1     number,
                                        num2     number,
                                        num3     number,
                                        num4     number) return number is

  flag number := 0;
begin
  if (type = 1) then
    if (memberid = member1) then
      flag := num1;
    else
      flag := 0;
    end if;
  elsif (type = 2) then
    if (memberid = member2) then
      flag := num2;
    else
      flag := 0;
    end if;
  elsif (type = 3) then
    if (memberid = member3) then
      flag := num3;
    else
      flag := 0;
    end if;
  elsif (type = 4) then
    if (memberid = member4) then
      flag := num4;
    else
      flag := 0;
    end if;

  else
    if (memberid = member1) then
      flag := num1;
    elsif (memberid = member2) then
      flag := num2;
    elsif (memberid = member3) then
      flag := num3;
    elsif (memberid = member4) then
      flag := num4;
    else
      flag := 0;
    end if;
  end if;

  return flag;

end;
