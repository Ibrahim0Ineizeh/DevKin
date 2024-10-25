provider "aws" {
  region = "us-west-2"  # Change to your desired region
}

resource "aws_ecs_cluster" "devkin_cluster" {
  name = "devkin-cluster"
}

resource "aws_s3_bucket" "minio_bucket" {
  bucket = "devkin-minio"
  acl    = "private"
}

resource "aws_s3_bucket_policy" "minio_bucket_policy" {
  bucket = aws_s3_bucket.minio_bucket.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = "*"
        Action = "s3:*"
        Resource = [
          "${aws_s3_bucket.minio_bucket.arn}/*",
          aws_s3_bucket.minio_bucket.arn,
        ]
      },
    ]
  })
}

resource "aws_ecs_task_definition" "mysql_task" {
  family                   = "mysql"
  network_mode            = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                     = "256"
  memory                  = "512"

  container_definitions = jsonencode([
    {
      name      = "mysql"
      image     = "mysql:latest"
      essential = true
      portMappings = [
        {
          containerPort = 3306
          hostPort      = 3306
          protocol      = "tcp"
        }
      ]
      environment = [
        {
          name  = "MYSQL_ROOT_PASSWORD"
          value = "userpassword"
        },
        {
          name  = "MYSQL_DATABASE"
          value = "devkin_db"
        },
        {
          name  = "MYSQL_USER"
          value = "user"
        },
        {
          name  = "MYSQL_PASSWORD"
          value = "userpassword"
        }
      ]
    }
  ])
}

resource "aws_ecs_task_definition" "backend_task" {
  family                   = "backend"
  network_mode            = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                     = "256"
  memory                  = "512"

  container_definitions = jsonencode([
    {
      name      = "backend"
      image     = "<YOUR_DOCKERHUB_USERNAME>/backend:latest"  # Change to your backend image
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]
      environment = [
        {
          name  = "SPRING_DATASOURCE_URL"
          value = "jdbc:mysql://mysql:3306/devkin_db"
        },
        {
          name  = "SPRING_DATASOURCE_USERNAME"
          value = "user"
        },
        {
          name  = "SPRING_DATASOURCE_PASSWORD"
          value = "userpassword"
        }
      ]
    }
  ])
}

resource "aws_ecs_task_definition" "frontend_task" {
  family                   = "frontend"
  network_mode            = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                     = "256"
  memory                  = "512"

  container_definitions = jsonencode([
    {
      name      = "frontend"
      image     = "<YOUR_DOCKERHUB_USERNAME>/frontend:latest"  # Change to your frontend image
      essential = true
      portMappings = [
        {
          containerPort = 80
          hostPort      = 80
          protocol      = "tcp"
        }
      ]
    }
  ])
}

resource "aws_ecs_task_definition" "minio_task" {
  family                   = "minio"
  network_mode            = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                     = "256"
  memory                  = "512"

  container_definitions = jsonencode([
    {
      name      = "minio"
      image     = "minio/minio"
      essential = true
      portMappings = [
        {
          containerPort = 9000
          hostPort      = 9000
          protocol      = "tcp"
        },
        {
          containerPort = 9001
          hostPort      = 9001
          protocol      = "tcp"
        }
      ]
      environment = [
        {
          name  = "MINIO_ACCESS_KEY"
          value = "minioadmin"
        },
        {
          name  = "MINIO_SECRET_KEY"
          value = "minioadmin"
        }
      ]
      command = ["server", "/data"]
    }
  ])
}

resource "aws_ecs_service" "mysql_service" {
  name            = "mysql-service"
  cluster         = aws_ecs_cluster.devkin_cluster.id
  task_definition = aws_ecs_task_definition.mysql_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = ["<YOUR_SUBNET_ID>"]  # Add your subnet ID
    security_groups  = ["<YOUR_SECURITY_GROUP_ID>"]  # Add your security group ID
    assign_public_ip = true
  }
}

resource "aws_ecs_service" "backend_service" {
  name            = "backend-service"
  cluster         = aws_ecs_cluster.devkin_cluster.id
  task_definition = aws_ecs_task_definition.backend_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = ["<YOUR_SUBNET_ID>"]  # Add your subnet ID
    security_groups  = ["<YOUR_SECURITY_GROUP_ID>"]  # Add your security group ID
    assign_public_ip = true
  }
}

resource "aws_ecs_service" "frontend_service" {
  name            = "frontend-service"
  cluster         = aws_ecs_cluster.devkin_cluster.id
  task_definition = aws_ecs_task_definition.frontend_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = ["<YOUR_SUBNET_ID>"]  # Add your subnet ID
    security_groups  = ["<YOUR_SECURITY_GROUP_ID>"]  # Add your security group ID
    assign_public_ip = true
  }
}

resource "aws_ecs_service" "minio_service" {
  name            = "minio-service"
  cluster         = aws_ecs_cluster.devkin_cluster.id
  task_definition = aws_ecs_task_definition.minio_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = ["<YOUR_SUBNET_ID>"]  # Add your subnet ID
    security_groups  = ["<YOUR_SECURITY_GROUP_ID>"]  # Add your security group ID
    assign_public_ip = true
  }
}
