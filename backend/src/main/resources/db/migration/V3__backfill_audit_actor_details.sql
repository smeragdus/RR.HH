update audit_logs a
set actor_name = concat(e.first_name, ' ', e.last_name),
    actor_dni = e.dni,
    actor_position = e.position
from users u
join employees e on e.id = u.employee_id
where a.user_id = u.id
  and (a.actor_name is null or a.actor_dni is null or a.actor_position is null);
