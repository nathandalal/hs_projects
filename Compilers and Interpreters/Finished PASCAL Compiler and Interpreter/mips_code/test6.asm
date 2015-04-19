	.text
	.globl main
	
main:
	la $t0, varcount
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	li $v0, 196
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	sw $v0, ($t0)
	la $t0, vartimes
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	li $v0, 0
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	sw $v0, ($t0)
	la $t0, varignore
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	li $v0, 10
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	li $v0, 13
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	subu $sp, $sp, 4
	sw $ra, ($sp) # PUSH
	jal procprintSquares
	lw $ra, ($sp)
	addu $sp, $sp, 4 # POP
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	sw $v0, ($t0)
	la $t0, varcount
	lw $v0, ($t0)
	move $a0, $v0
	li $v0, 1
	syscall # prints number
	la $a0, nextln
	li $v0, 4
	syscall # moves to nextln
	la $t0, vartimes
	lw $v0, ($t0)
	move $a0, $v0
	li $v0, 1
	syscall # prints number
	la $a0, nextln
	li $v0, 4
	syscall # moves to nextln
	
	li $v0 10
	syscall	# halt
	
procprintSquares:
	li $t0, 0
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	lw $v0, 20($sp)
	sw $v0, 0($sp)
whileloop1:
	lw $v0, 0($sp)
	move $t0, $v0
	lw $v0, 16($sp)
	move $t1, $v0
	bgt $t0, $t1, endwhileloop1
	lw $v0, 0($sp)
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	lw $v0, 4($sp)
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	# now num1 in $t0 and num2 in $v0
	mult $t0, $v0 # divides $v0 from $t0
	mflo $v0 # puts remainder in $v0
	sw $v0, 4($sp)
	lw $v0, 4($sp)
	move $a0, $v0
	li $v0, 1
	syscall # prints number
	la $a0, nextln
	li $v0, 4
	syscall # moves to nextln
	lw $v0, 0($sp)
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	li $v0, 1
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	# now num1 in $t0 and num2 in $v0
	addu $v0, $t0, $v0 # adds and stores in $v0
	sw $v0, 0($sp)
	la $t0, vartimes
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	la $t0, vartimes
	lw $v0, ($t0)
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	li $v0, 1
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	# now num1 in $t0 and num2 in $v0
	addu $v0, $t0, $v0 # adds and stores in $v0
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	sw $v0, ($t0)
	j whileloop1
endwhileloop1:
	lw $v0, ($sp)
	addu $sp, $sp, 4 # POP
	lw $v0, ($sp)
	addu $sp, $sp, 4 # POP
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	lw $v0, ($sp)
	addu $sp, $sp, 4 # POP
	lw $v0, ($sp)
	addu $sp, $sp, 4 # POP
	move $v0, $t0
	jr $ra # end of procprintSquares
	
	.data
	
varcount:
	.word 0
varignore:
	.word 0
vartimes:
	.word 0
nextln:
	.asciiz "\n"
