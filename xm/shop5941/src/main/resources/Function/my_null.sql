create or replace function my_null(type varchar2) return number is

begin

  if type is null then
    return 0;
  else
    return 1;
  end if;

end;
