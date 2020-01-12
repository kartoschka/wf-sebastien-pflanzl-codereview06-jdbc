-- get all pupils from class with classID == 1
select * from pupil where class_id = 1;

-- get all pupils from class with class-name == "1a"
select pupil.id, first_name, last_name, name as class_name
from pupil join class on class_id = class.id
where name = "1a";
